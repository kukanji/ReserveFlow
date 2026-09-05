<script lang="ts">
import { defineComponent, type PropType } from 'vue'
import DatePickerCalendar from '@/components/layout/DatePickerCalendar.vue'

export default defineComponent({
  name: 'ReservationToolbar',
  components: {
    DatePickerCalendar,
  },
  props: {
    dateLabel: {
      type: String,
      required: true,
    },
    selectedDate: {
      type: Date as PropType<Date>,
      required: true,
    },
  },
  emits: ['prev-date', 'next-date', 'select-date', 'open-create'],
  data() {
    return {
      isCalendarOpen: false,
    }
  },
  mounted() {
    document.addEventListener('click', this.handleOutsideClick)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleOutsideClick)
  },
  methods: {
    toggleCalendar() {
      this.isCalendarOpen = !this.isCalendarOpen
    },
    handleOutsideClick(event: MouseEvent) {
      if (!this.isCalendarOpen) return
      const container = this.$refs.dateNav as HTMLElement | undefined
      if (container && !container.contains(event.target as Node)) {
        this.isCalendarOpen = false
      }
    },
    onSelectDate(date: Date) {
      this.$emit('select-date', date)
      this.isCalendarOpen = false
    },
  },
})
</script>

<template>
  <header class="toolbar">
    <div class="date-nav" ref="dateNav">
      <button type="button" class="icon-button" @click="$emit('prev-date')" aria-label="前日">‹</button>
      <button type="button" class="icon-button" @click="$emit('next-date')" aria-label="翌日">›</button>
      <button type="button" class="date-label" @click="toggleCalendar">{{ dateLabel }}</button>

      <div v-if="isCalendarOpen" class="calendar-popover">
        <DatePickerCalendar :selected-date="selectedDate" @select="onSelectDate" />
      </div>
    </div>
    <div class="toolbar-actions">
      <input class="search-input" type="search" placeholder="検索" />
      <button type="button" class="create-button" @click="$emit('open-create')">+ 作成</button>
    </div>
  </header>
</template>

<style scoped>
.toolbar {
  display: grid;
  grid-template-columns: 1fr 1fr;
  align-items: center;
  flex-shrink: 0;
  padding: 15px 20px;
  border-bottom: 1px solid var(--color-border);
}

.date-nav {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.icon-button {
  width: 40px;
  height: 40px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background: var(--color-background);
  color: var(--color-text);
  cursor: pointer;
  font-size: 30px;
  line-height: 1;
}

.icon-button:hover {
  border-color: var(--color-border-hover);
}

.date-label {
  font-size: 30px;
  font-weight: 700;
  border: none;
  background: none;
  color: var(--color-text);
  cursor: pointer;
  padding: 0;
}

.calendar-popover {
  position: absolute;
  top: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.search-input {
  padding: 6px 12px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background: var(--color-background);
  color: var(--color-text);
  font-size: 25px;
}

.create-button {
  padding: 5px 25px;
  border: none;
  border-radius: 6px;
  background: var(--color-primary);
  color: white;
  font-size: 25px;
  font-weight: 700;
  cursor: pointer;
}
</style>
