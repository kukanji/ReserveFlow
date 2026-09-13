<script lang="ts">
import { defineComponent, type PropType } from 'vue'
import type { Reservation } from '@/types/reservation'

export default defineComponent({
  name: 'ReservationCard',
  props: {
    reservation: {
      type: Object as PropType<Reservation>,
      required: true,
    },
    startRow: {
      type: Number,
      required: true,
    },
    rowSpan: {
      type: Number,
      required: true,
    },
    column: {
      type: Number,
      required: true,
    },
  },
  emits: ['open-edit'],
  computed: {
    timeRange(): string {
      return `${this.formatTime(this.reservation.startTime)}〜${this.formatTime(this.reservation.endTime)}`
    },
    style(): Record<string, string> {
      return {
        gridRow: `${this.startRow} / span ${this.rowSpan}`,
        gridColumn: String(this.column),
      }
    },
  },
  methods: {
    formatTime(value: string): string {
      return value.slice(11, 16)
    },
  },
})
</script>

<template>
  <button type="button" class="reservation-card" :style="style" @click="$emit('open-edit', reservation)">
    <p class="customer-name">{{ reservation.customerName }}</p>
    <p class="menu-name">{{ reservation.menuName }}</p>
    <p class="time-range">{{ timeRange }}</p>
  </button>
</template>

<style scoped>
.reservation-card {
  display: block;
  width: 100%;
  margin: 1px 4px;
  padding: 4px 8px;
  border: none;
  border-left: 3px solid var(--color-primary);
  border-radius: 4px;
  background: var(--color-primary-soft);
  overflow: hidden;
  line-height: 1.25;
  text-align: left;
  font-family: inherit;
  cursor: pointer;
}

.reservation-card:hover {
  filter: brightness(0.96);
}

.customer-name {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-primary);
}

.menu-name,
.time-range {
  font-size: 18px;
  color: var(--color-primary);
  opacity: 0.85;
}
</style>
