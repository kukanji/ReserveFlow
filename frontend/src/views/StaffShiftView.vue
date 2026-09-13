<script lang="ts">
import { defineComponent } from 'vue'
import { fetchStaffList } from '@/api/staff'
import { fetchShifts } from '@/api/shifts'
import type { Staff } from '@/types/staff'
import type { Shift } from '@/types/shift'
import AddStaffDialog from '@/components/staff/AddStaffDialog.vue'
import DeleteStaffDialog from '@/components/staff/DeleteStaffDialog.vue'
import ShiftDayDialog from '@/components/staff/ShiftDayDialog.vue'
import ShiftWeekDialog from '@/components/staff/ShiftWeekDialog.vue'

const WEEKDAYS = ['日', '月', '火', '水', '木', '金', '土']

interface WeekDay {
  date: Date
  dateKey: string
  mainLabel: string
  subLabel: string
}

type DialogName = 'addStaff' | 'deleteStaff' | 'shiftDay' | 'shiftWeek' | null

function toDateKey(date: Date): string {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function mondayOf(date: Date): Date {
  const result = new Date(date.getFullYear(), date.getMonth(), date.getDate())
  const day = result.getDay()
  const diff = day === 0 ? -6 : 1 - day
  result.setDate(result.getDate() + diff)
  return result
}

export default defineComponent({
  name: 'StaffShiftView',
  inheritAttrs: false,
  components: {
    AddStaffDialog,
    DeleteStaffDialog,
    ShiftDayDialog,
    ShiftWeekDialog,
  },
  data() {
    return {
      weekStart: mondayOf(new Date()),
      staffList: [] as Staff[],
      shifts: [] as Shift[],
      isLoading: true,
      activeDialog: null as DialogName,
      deleteTargetStaff: null as Staff | null,
      shiftDayContext: null as { staffId: number; dateKey: string; existing: Shift | null } | null,
    }
  },
  computed: {
    weekDays(): WeekDay[] {
      return Array.from({ length: 7 }, (_, i) => {
        const date = new Date(this.weekStart.getFullYear(), this.weekStart.getMonth(), this.weekStart.getDate() + i)
        return {
          date,
          dateKey: toDateKey(date),
          mainLabel: `${date.getDate()}（${WEEKDAYS[date.getDay()]}）`,
          subLabel: `${date.getMonth() + 1}/${date.getDate()}`,
        }
      })
    },
    weekRangeLabel(): string {
      const start = this.weekDays[0]!.date
      const end = this.weekDays[6]!.date
      const startLabel = `${start.getFullYear()}年${start.getMonth() + 1}月${start.getDate()}日（${WEEKDAYS[start.getDay()]}）`
      const endLabel = `${end.getMonth() + 1}月${end.getDate()}日（${WEEKDAYS[end.getDay()]}）`
      return `${startLabel} 〜 ${endLabel}`
    },
    shiftsByKey(): Map<string, Shift> {
      const map = new Map<string, Shift>()
      for (const shift of this.shifts) {
        map.set(`${shift.staffId}-${shift.workDate}`, shift)
      }
      return map
    },
    rows(): { staff: Staff; cells: { dateKey: string; shift: Shift | null }[] }[] {
      return this.staffList.map((staff) => ({
        staff,
        cells: this.weekDays.map((day) => ({
          dateKey: day.dateKey,
          shift: this.shiftsByKey.get(`${staff.id}-${day.dateKey}`) ?? null,
        })),
      }))
    },
  },
  async mounted() {
    await this.loadData()
  },
  methods: {
    formatTime(value: string | null): string {
      return value ? value.slice(0, 5) : ''
    },
    async loadData() {
      this.isLoading = true
      try {
        const start = this.weekDays[0]!.dateKey
        const end = this.weekDays[6]!.dateKey
        const [staffData, shiftData] = await Promise.all([fetchStaffList(), fetchShifts(start, end)])
        this.staffList = staffData
        this.shifts = shiftData
      } finally {
        this.isLoading = false
      }
    },
    shiftWeek(weeks: number) {
      this.weekStart = new Date(
        this.weekStart.getFullYear(),
        this.weekStart.getMonth(),
        this.weekStart.getDate() + weeks * 7,
      )
      this.loadData()
    },
    openAddStaff() {
      this.activeDialog = 'addStaff'
    },
    openDeleteStaff(staff: Staff) {
      this.deleteTargetStaff = staff
      this.activeDialog = 'deleteStaff'
    },
    openShiftWeekDialog() {
      this.activeDialog = 'shiftWeek'
    },
    openShiftDayDialog(staffId: number, dateKey: string) {
      this.shiftDayContext = {
        staffId,
        dateKey,
        existing: this.shiftsByKey.get(`${staffId}-${dateKey}`) ?? null,
      }
      this.activeDialog = 'shiftDay'
    },
    closeDialogs() {
      this.activeDialog = null
      this.deleteTargetStaff = null
      this.shiftDayContext = null
    },
    async handleStaffCreated() {
      this.closeDialogs()
      await this.loadData()
    },
    async handleStaffDeleted() {
      this.closeDialogs()
      await this.loadData()
    },
    async handleShiftsSaved() {
      this.closeDialogs()
      await this.loadData()
    },
  },
})
</script>

<template>
  <main class="shift-page">
    <header class="topbar">
      <h1>シフト管理</h1>
      <div class="week-nav">
        <button type="button" @click="shiftWeek(-1)" aria-label="前週">‹</button>
        <span class="range">{{ weekRangeLabel }}</span>
        <button type="button" @click="shiftWeek(1)" aria-label="翌週">›</button>
      </div>
      <div class="actions">
        <button type="button" class="btn" @click="openAddStaff">＋ スタッフを追加</button>
        <button type="button" class="btn primary" @click="openShiftWeekDialog">＋ シフトを追加</button>
      </div>
    </header>

    <p v-if="isLoading" class="loading-message">読み込み中...</p>

    <div v-else class="grid-wrap">
      <table class="shift-grid">
        <thead>
          <tr>
            <th class="col-staff">スタッフ</th>
            <th v-for="day in weekDays" :key="day.dateKey" class="col-day">
              {{ day.mainLabel }}
              <span class="youbi">{{ day.subLabel }}</span>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in rows" :key="row.staff.id">
            <td class="staff-cell">
              <div class="staff-cell-inner">
                <span class="staff-name">{{ row.staff.name }}</span>
                <button
                  type="button"
                  class="staff-delete"
                  aria-label="スタッフを削除"
                  @click="openDeleteStaff(row.staff)"
                >
                  ×
                </button>
              </div>
            </td>
            <td v-for="cell in row.cells" :key="cell.dateKey" class="day-cell">
              <button
                v-if="cell.shift && cell.shift.off"
                type="button"
                class="shift-off"
                @click="openShiftDayDialog(row.staff.id, cell.dateKey)"
              >
                休み
              </button>
              <button
                v-else-if="cell.shift"
                type="button"
                class="shift-block"
                @click="openShiftDayDialog(row.staff.id, cell.dateKey)"
              >
                <div class="time">{{ formatTime(cell.shift.startTime) }}〜{{ formatTime(cell.shift.endTime) }}</div>
                <div v-if="cell.shift.task" class="task">{{ cell.shift.task }}</div>
              </button>
              <button
                v-else
                type="button"
                class="cell-empty"
                aria-label="シフトを追加"
                @click="openShiftDayDialog(row.staff.id, cell.dateKey)"
              >
                ＋
              </button>
            </td>
          </tr>
          <tr v-if="rows.length === 0">
            <td class="empty-row" :colspan="8">スタッフが登録されていません。</td>
          </tr>
        </tbody>
      </table>
    </div>

    <AddStaffDialog v-if="activeDialog === 'addStaff'" @close="closeDialogs" @created="handleStaffCreated" />

    <DeleteStaffDialog
      v-if="activeDialog === 'deleteStaff' && deleteTargetStaff"
      :staff="deleteTargetStaff"
      @close="closeDialogs"
      @deleted="handleStaffDeleted"
    />

    <ShiftDayDialog
      v-if="activeDialog === 'shiftDay' && shiftDayContext"
      :staff-list="staffList"
      :staff-id="shiftDayContext.staffId"
      :date-key="shiftDayContext.dateKey"
      :existing="shiftDayContext.existing"
      @close="closeDialogs"
      @saved="handleShiftsSaved"
    />

    <ShiftWeekDialog
      v-if="activeDialog === 'shiftWeek'"
      :staff-list="staffList"
      :week-days="weekDays"
      :shifts-by-key="shiftsByKey"
      @close="closeDialogs"
      @saved="handleShiftsSaved"
    />
  </main>
</template>

<style scoped>
.shift-page {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  padding: 24px 32px;
  overflow: auto;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
}

.topbar h1 {
  font-size: 24px;
  font-weight: 700;
}

.week-nav {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
}

.week-nav button {
  width: 32px;
  height: 32px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background: var(--color-background);
  color: var(--color-text);
  cursor: pointer;
  font-size: 16px;
}

.week-nav button:hover {
  border-color: var(--color-border-hover);
}

.week-nav .range {
  font-weight: 700;
}

.actions {
  display: flex;
  gap: 10px;
}

.btn {
  font-size: 15px;
  padding: 8px 16px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
  background: var(--color-background);
  cursor: pointer;
}

.btn:hover {
  border-color: var(--color-border-hover);
}

.btn.primary {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: white;
  font-weight: 700;
}

.loading-message {
  color: var(--color-text-muted);
}

.grid-wrap {
  flex: 1;
  min-height: 0;
  overflow: auto;
  border: 1px solid var(--color-border);
  border-radius: 8px;
}

.shift-grid {
  border-collapse: collapse;
  width: 100%;
  table-layout: fixed;
}

.shift-grid th,
.shift-grid td {
  border: 1px solid var(--color-border);
  padding: 0;
}

.col-staff {
  width: 160px;
  min-width: 160px;
}

.col-day {
  width: 170px;
  min-width: 170px;
}

.shift-grid thead th {
  position: sticky;
  top: 0;
  background: var(--color-background-soft);
  padding: 12px 8px;
  text-align: center;
  font-weight: 700;
  font-size: 15px;
  z-index: 1;
}

.shift-grid thead th.col-staff {
  left: 0;
  z-index: 2;
}

.youbi {
  display: block;
  margin-top: 2px;
  font-size: 12px;
  font-weight: 400;
  color: var(--color-text-muted);
}

.staff-cell {
  position: sticky;
  left: 0;
  background: var(--color-background-soft);
  padding: 12px 14px;
  z-index: 1;
}

.staff-cell-inner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.staff-name {
  font-weight: 700;
  font-size: 15px;
}

.staff-delete {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background: var(--color-background);
  color: var(--color-text-muted);
  cursor: pointer;
  font-size: 14px;
  opacity: 0;
  flex: none;
}

.staff-cell:hover .staff-delete {
  opacity: 1;
}

.staff-delete:hover {
  border-color: #d1442f;
  color: #d1442f;
}

.day-cell {
  height: 76px;
  vertical-align: top;
  padding: 4px;
}

.shift-block {
  width: 100%;
  height: 100%;
  background: var(--color-primary-soft);
  border: 1px solid var(--color-primary);
  border-radius: 6px;
  padding: 6px 8px;
  font-size: 13px;
  line-height: 1.4;
  text-align: left;
  cursor: pointer;
  color: var(--color-primary);
}

.shift-block .time {
  font-weight: 700;
}

.shift-block .task {
  color: var(--color-primary);
  opacity: 0.8;
  font-size: 12px;
}

.shift-off {
  width: 100%;
  height: 100%;
  background: #fdeaea;
  border: 1px solid #e08a8a;
  border-radius: 6px;
  color: #b23b3b;
  font-weight: 700;
  font-size: 13px;
  cursor: pointer;
}

.cell-empty {
  width: 100%;
  height: 100%;
  border: none;
  background: none;
  color: var(--color-text-muted);
  font-size: 16px;
  cursor: pointer;
  opacity: 0;
}

.day-cell:hover .cell-empty {
  opacity: 1;
}

.empty-row {
  padding: 20px;
  text-align: center;
  color: var(--color-text-muted);
}
</style>
