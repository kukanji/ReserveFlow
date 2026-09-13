<script lang="ts">
import { defineComponent, type PropType } from 'vue'
import { bulkUpsertShifts } from '@/api/shifts'
import type { Staff } from '@/types/staff'
import type { Shift } from '@/types/shift'
import { SCHEDULE_START_HOUR, SCHEDULE_END_HOUR, SCHEDULE_SLOT_MINUTES } from '@/constants/schedule'

interface WeekDayProp {
  date: Date
  dateKey: string
  mainLabel: string
  subLabel: string
}

interface DayRow {
  dateKey: string
  mainLabel: string
  subLabel: string
  off: boolean
  startTime: string
  endTime: string
  task: string
}

function buildTimeOptions(): string[] {
  const options: string[] = []
  const totalMinutes = (SCHEDULE_END_HOUR - SCHEDULE_START_HOUR) * 60
  for (let minutes = 0; minutes <= totalMinutes; minutes += SCHEDULE_SLOT_MINUTES) {
    const hour = SCHEDULE_START_HOUR + Math.floor(minutes / 60)
    const minute = minutes % 60
    options.push(`${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`)
  }
  return options
}

export default defineComponent({
  name: 'ShiftWeekDialog',
  props: {
    staffList: {
      type: Array as PropType<Staff[]>,
      required: true,
    },
    weekDays: {
      type: Array as PropType<WeekDayProp[]>,
      required: true,
    },
    shiftsByKey: {
      type: Object as PropType<Map<string, Shift>>,
      required: true,
    },
  },
  emits: ['close', 'saved'],
  data() {
    return {
      selectedStaffId: null as number | null,
      days: [] as DayRow[],
      isSubmitting: false,
      errorMessage: '',
    }
  },
  computed: {
    timeOptions(): string[] {
      return buildTimeOptions()
    },
    weekRangeLabel(): string {
      if (this.weekDays.length === 0) return ''
      return `対象週：${this.weekDays[0]!.subLabel} 〜 ${this.weekDays[6]!.subLabel}`
    },
    canSubmit(): boolean {
      return this.selectedStaffId !== null
    },
  },
  watch: {
    selectedStaffId() {
      this.buildDays()
    },
  },
  methods: {
    buildDays() {
      if (this.selectedStaffId === null) {
        this.days = []
        return
      }
      this.days = this.weekDays.map((day) => {
        const existing = this.shiftsByKey.get(`${this.selectedStaffId}-${day.dateKey}`)
        return {
          dateKey: day.dateKey,
          mainLabel: day.mainLabel,
          subLabel: day.subLabel,
          off: existing?.off ?? false,
          startTime: existing?.startTime?.slice(0, 5) ?? '09:00',
          endTime: existing?.endTime?.slice(0, 5) ?? '18:00',
          task: existing?.task ?? '',
        }
      })
    },
    bulkFillTimes() {
      const firstWorkingDay = this.days.find((d) => !d.off)
      if (!firstWorkingDay) return
      const { startTime, endTime } = firstWorkingDay
      this.days.forEach((day) => {
        if (!day.off) {
          day.startTime = startTime
          day.endTime = endTime
        }
      })
    },
    async submit() {
      if (!this.canSubmit || this.isSubmitting) return
      this.isSubmitting = true
      this.errorMessage = ''
      try {
        await bulkUpsertShifts({
          staffId: this.selectedStaffId as number,
          days: this.days.map((day) => ({
            workDate: day.dateKey,
            off: day.off,
            startTime: day.off ? null : day.startTime,
            endTime: day.off ? null : day.endTime,
            task: day.off ? null : day.task.trim() === '' ? null : day.task.trim(),
          })),
        })
        this.$emit('saved')
      } catch {
        this.errorMessage = '保存に失敗しました。'
      } finally {
        this.isSubmitting = false
      }
    },
  },
})
</script>

<template>
  <div class="overlay" @click.self="$emit('close')">
    <div class="dialog">
      <div class="dialog-header">
        <h2>シフトを追加</h2>
        <button type="button" class="close-button" @click="$emit('close')" aria-label="閉じる">×</button>
      </div>

      <div class="dialog-body">
        <div class="week-select-row">
          <span class="range">{{ weekRangeLabel }}</span>
        </div>

        <label class="field staff-select-row">
          <span class="field-label">スタッフを選択</span>
          <select v-model.number="selectedStaffId">
            <option :value="null" disabled>選択してください</option>
            <option v-for="staff in staffList" :key="staff.id" :value="staff.id">{{ staff.name }}</option>
          </select>
        </label>

        <div v-if="days.length > 0" class="day-table-wrap">
          <table class="day-input-table">
            <thead>
              <tr>
                <th class="day-label-cell">曜日</th>
                <th class="off-toggle-cell">休み</th>
                <th class="time-cell">開始</th>
                <th class="time-cell">終了</th>
                <th class="task-cell">担当業務</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="day in days" :key="day.dateKey" :class="{ 'is-off': day.off }">
                <td class="day-label-cell">
                  {{ day.mainLabel }}
                  <span class="d-sub">{{ day.subLabel }}</span>
                </td>
                <td class="off-toggle-cell">
                  <input type="checkbox" v-model="day.off" />
                </td>
                <td class="time-cell">
                  <select v-model="day.startTime" :disabled="day.off">
                    <option v-for="time in timeOptions" :key="time" :value="time">{{ time }}</option>
                  </select>
                </td>
                <td class="time-cell">
                  <select v-model="day.endTime" :disabled="day.off">
                    <option v-for="time in timeOptions" :key="time" :value="time">{{ time }}</option>
                  </select>
                </td>
                <td class="task-cell">
                  <input v-model="day.task" type="text" placeholder="任意" :disabled="day.off" />
                </td>
              </tr>
            </tbody>
          </table>
          <span class="bulk-fill" @click="bulkFillTimes">⟳ 出勤日すべてに先頭の時間を一括入力</span>
        </div>

        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      </div>

      <div class="dialog-footer">
        <button type="button" class="cancel-button" @click="$emit('close')">キャンセル</button>
        <button type="button" class="submit-button" :disabled="!canSubmit || isSubmitting" @click="submit">
          保存する
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.dialog {
  width: 640px;
  max-height: 90vh;
  overflow-y: auto;
  background: var(--color-background);
  border-radius: 14px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.2);
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px;
  border-bottom: 1px solid var(--color-border);
}

.dialog-header h2 {
  font-size: 20px;
  font-weight: 700;
}

.close-button {
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  font-size: 22px;
  color: var(--color-text-muted);
  cursor: pointer;
}

.dialog-body {
  padding: 22px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.week-select-row {
  padding: 10px 12px;
  background: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  font-weight: 700;
  font-size: 14px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 13px;
  color: var(--color-text-muted);
}

input,
select {
  padding: 8px 10px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: var(--color-background);
  color: var(--color-text);
  font-size: 14px;
  font-family: inherit;
}

input:disabled,
select:disabled {
  background: var(--color-background-mute);
  color: var(--color-text-muted);
}

.day-table-wrap {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.day-input-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.day-input-table th {
  text-align: left;
  color: var(--color-text-muted);
  font-weight: 500;
  padding: 4px 6px;
  border-bottom: 1px solid var(--color-border);
}

.day-input-table td {
  padding: 6px;
  border-bottom: 1px solid var(--color-border);
  vertical-align: middle;
}

.day-label-cell {
  width: 70px;
  font-weight: 700;
  white-space: nowrap;
}

.d-sub {
  display: block;
  font-weight: 400;
  color: var(--color-text-muted);
  font-size: 11px;
}

.off-toggle-cell {
  width: 48px;
  text-align: center;
}

.time-cell {
  width: 100px;
}

.time-cell select {
  width: 100%;
}

.task-cell input {
  width: 100%;
}

.bulk-fill {
  align-self: flex-start;
  font-size: 13px;
  color: var(--color-primary);
  cursor: pointer;
}

.error-message {
  color: #d0342c;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 18px 22px;
  border-top: 1px solid var(--color-border);
}

.cancel-button {
  padding: 10px 18px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: var(--color-background);
  font-size: 15px;
  cursor: pointer;
}

.submit-button {
  padding: 10px 18px;
  border: none;
  border-radius: 8px;
  background: var(--color-primary);
  color: white;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
}

.submit-button:disabled {
  opacity: 0.5;
  cursor: default;
}
</style>
