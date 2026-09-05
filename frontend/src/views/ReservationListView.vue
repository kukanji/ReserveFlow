<script lang="ts">
import { defineComponent, type PropType } from 'vue'
import { fetchReservations } from '@/api/reservations'
import { fetchStaffList } from '@/api/staff'
import type { Reservation } from '@/types/reservation'
import type { Staff } from '@/types/staff'
import ReservationCard from '@/components/reservation/ReservationCard.vue'

const SLOT_MINUTES = 30
const START_HOUR = 7
const END_HOUR = 21
const SLOT_COUNT = ((END_HOUR - START_HOUR) * 60) / SLOT_MINUTES
const HEADER_ROW_COUNT = 1

interface PlacedReservation {
  reservation: Reservation
  startRow: number
  rowSpan: number
  column: number
}

export default defineComponent({
  name: 'ReservationListView',
  components: {
    ReservationCard,
  },
  props: {
    selectedDate: {
      type: Date as PropType<Date>,
      required: true,
    },
    reservationsVersion: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      reservations: [] as Reservation[],
      staffList: [] as Staff[],
      isLoading: true,
      errorMessage: '',
    }
  },
  computed: {
    selectedDateKey(): string {
      return this.toDateKey(this.selectedDate)
    },
    timeSlots(): { row: number; hour: number; minute: number }[] {
      return Array.from({ length: SLOT_COUNT }, (_, i) => {
        const minutesFromStart = i * SLOT_MINUTES
        const hour = START_HOUR + Math.floor(minutesFromStart / 60)
        const minute = minutesFromStart % 60
        return { row: i + 1 + HEADER_ROW_COUNT, hour, minute }
      })
    },
    reservationsForDate(): Reservation[] {
      return this.reservations.filter((r) => r.startTime.slice(0, 10) === this.selectedDateKey)
    },
    placedReservations(): PlacedReservation[] {
      const staffIndex = new Map(this.staffList.map((s, i) => [s.id, i]))
      return this.reservationsForDate
        .filter((r) => staffIndex.has(r.staffId))
        .map((r) => {
          const startOffset = this.timeToSlotOffset(r.startTime)
          const endOffset = this.timeToSlotOffset(r.endTime)
          return {
            reservation: r,
            startRow: Math.max(
              1 + HEADER_ROW_COUNT,
              Math.round(startOffset) + 1 + HEADER_ROW_COUNT,
            ),
            rowSpan: Math.max(1, Math.round(endOffset - startOffset)),
            column: (staffIndex.get(r.staffId) ?? 0) + 2,
          }
        })
    },
    gridTemplateColumns(): string {
      return `100px repeat(${this.staffList.length}, 1fr)`
    },
    gridTemplateRows(): string {
      return `auto repeat(${SLOT_COUNT}, 60px)`
    },
  },
  watch: {
    reservationsVersion() {
      this.loadReservations()
    },
  },
  async mounted() {
    await Promise.all([this.loadReservations(), this.loadStaffList()])
  },
  methods: {
    async loadReservations() {
      try {
        this.reservations = await fetchReservations()
      } catch {
        this.errorMessage = '予約データの取得に失敗しました。'
      } finally {
        this.isLoading = false
      }
    },
    async loadStaffList() {
      this.staffList = await fetchStaffList()
    },
    toDateKey(date: Date): string {
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    formatHourLabel(hour: number): string {
      return `${String(hour).padStart(2, '0')}:00`
    },
    timeToSlotOffset(value: string): number {
      const hour = Number(value.slice(11, 13))
      const minute = Number(value.slice(14, 16))
      return ((hour - START_HOUR) * 60 + minute) / SLOT_MINUTES
    },
  },
})
</script>

<template>
  <main class="reservation-list">
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <p v-else-if="isLoading" class="loading-message">読み込み中...</p>

    <div v-else class="schedule">
      <div
        class="schedule-grid"
        :style="{ gridTemplateColumns, gridTemplateRows }"
      >
        <div
          v-for="staff in staffList"
          :key="staff.id"
          class="staff-header"
          :style="{ gridColumn: staffList.indexOf(staff) + 2 }"
        >
          {{ staff.name }}
        </div>

        <template v-for="slot in timeSlots" :key="slot.row">
          <div
            class="time-label"
            :style="{ gridRow: slot.row }"
          >
            {{ slot.minute === 0 ? formatHourLabel(slot.hour) : '' }}
          </div>
          <div
            v-for="(staff, colIndex) in staffList"
            :key="`${slot.row}-${staff.id}`"
            class="slot-cell"
            :class="{ 'slot-cell--hour': slot.minute === 0 }"
            :style="{ gridRow: slot.row, gridColumn: colIndex + 2 }"
          />
        </template>

        <ReservationCard
          v-for="p in placedReservations"
          :key="p.reservation.id"
          :reservation="p.reservation"
          :start-row="p.startRow"
          :row-span="p.rowSpan"
          :column="p.column"
        />
      </div>
    </div>
  </main>
</template>

<style scoped>
.reservation-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  padding: 24px 32px;
  overflow: auto;
}

.error-message {
  color: #d0342c;
}

.loading-message {
  color: var(--color-text-muted);
}

.schedule {
  flex: 1;
  min-height: 0;
  overflow: auto;
  border: 1px solid var(--color-border);
  border-radius: 8px;
}

.schedule-grid {
  display: grid;
  position: relative;
}

.staff-header {
  position: sticky;
  top: 0;
  grid-row: 1;
  padding: 12px 8px;
  font-size: 20px;
  font-weight: 700;
  text-align: center;
  background: var(--color-background);
  border-bottom: 1px solid var(--color-border);
  border-left: 1px solid var(--color-border);
  z-index: 1;
}

.time-label {
  grid-column: 1;
  padding: 0 8px;
  line-height: 1;
  font-size: 18px;
  color: var(--color-text-muted);
  white-space: nowrap;
}

.slot-cell {
  border-left: 1px solid var(--color-border);
  border-top: 1px solid var(--color-border);
}

.slot-cell--hour {
  border-top: 1px solid var(--color-border-hover);
}
</style>
