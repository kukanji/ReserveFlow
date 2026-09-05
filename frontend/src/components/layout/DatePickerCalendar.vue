<script lang="ts">
import { defineComponent, type PropType } from 'vue'

interface CalendarDay {
  date: Date
  dateKey: string
  isCurrentMonth: boolean
}

const WEEKDAYS = ['日', '月', '火', '水', '木', '金', '土']

export default defineComponent({
  name: 'DatePickerCalendar',
  props: {
    selectedDate: {
      type: Date as PropType<Date>,
      required: true,
    },
  },
  emits: ['select', 'close'],
  data() {
    return {
      displayedMonth: new Date(this.selectedDate.getFullYear(), this.selectedDate.getMonth(), 1),
      weekdays: WEEKDAYS,
    }
  },
  computed: {
    monthLabel(): string {
      return `${this.displayedMonth.getFullYear()}年${this.displayedMonth.getMonth() + 1}月`
    },
    selectedDateKey(): string {
      return this.toDateKey(this.selectedDate)
    },
    todayKey(): string {
      return this.toDateKey(new Date())
    },
    calendarDays(): CalendarDay[] {
      const year = this.displayedMonth.getFullYear()
      const month = this.displayedMonth.getMonth()
      const firstDayOfMonth = new Date(year, month, 1)
      const startDate = new Date(year, month, 1 - firstDayOfMonth.getDay())

      return Array.from({ length: 42 }, (_, i) => {
        const date = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate() + i)
        return {
          date,
          dateKey: this.toDateKey(date),
          isCurrentMonth: date.getMonth() === month,
        }
      })
    },
  },
  methods: {
    toDateKey(date: Date): string {
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    shiftMonth(months: number) {
      this.displayedMonth = new Date(
        this.displayedMonth.getFullYear(),
        this.displayedMonth.getMonth() + months,
        1,
      )
    },
    selectDay(day: CalendarDay) {
      this.$emit('select', day.date)
    },
  },
})
</script>

<template>
  <div class="calendar">
    <div class="calendar-header">
      <button type="button" class="month-nav" @click="shiftMonth(-1)" aria-label="前月">‹</button>
      <span class="month-label">{{ monthLabel }}</span>
      <button type="button" class="month-nav" @click="shiftMonth(1)" aria-label="翌月">›</button>
    </div>
    <div class="weekday-row">
      <span v-for="weekday in weekdays" :key="weekday" class="weekday-cell">{{ weekday }}</span>
    </div>
    <div class="day-grid">
      <button
        v-for="day in calendarDays"
        :key="day.dateKey"
        type="button"
        class="day-cell"
        :class="{
          'is-outside-month': !day.isCurrentMonth,
          'is-selected': day.dateKey === selectedDateKey,
          'is-today': day.dateKey === todayKey,
        }"
        @click="selectDay(day)"
      >
        {{ day.date.getDate() }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.calendar {
  width: 560px;
  padding: 24px;
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.12);
}

.calendar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.month-nav {
  width: 56px;
  height: 56px;
  border: 1px solid var(--color-border);
  border-radius: 12px;
  background: var(--color-background);
  color: var(--color-text);
  cursor: pointer;
  font-size: 32px;
  line-height: 1;
}

.month-nav:hover {
  border-color: var(--color-border-hover);
}

.month-label {
  font-size: 28px;
  font-weight: 700;
}

.weekday-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 8px;
}

.weekday-cell {
  font-size: 24px;
  color: var(--color-text-muted);
  text-align: center;
}

.day-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.day-cell {
  aspect-ratio: 1;
  border: none;
  border-radius: 12px;
  background: none;
  color: var(--color-text);
  font-size: 26px;
  cursor: pointer;
}

.day-cell:hover {
  background: var(--color-background-soft);
}

.day-cell.is-outside-month {
  color: var(--color-text-muted);
}

.day-cell.is-today {
  font-weight: 700;
  color: var(--color-primary);
}

.day-cell.is-selected {
  background: var(--color-primary);
  color: white;
}
</style>
