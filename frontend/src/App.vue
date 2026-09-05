<script lang="ts">
import { defineComponent } from 'vue'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import ReservationToolbar from '@/components/layout/ReservationToolbar.vue'
import ReservationCreateDialog from '@/components/reservation/ReservationCreateDialog.vue'

export default defineComponent({
  name: 'App',
  components: {
    AppSidebar,
    ReservationToolbar,
    ReservationCreateDialog,
  },
  data() {
    return {
      selectedDate: new Date(),
      isCreateDialogOpen: false,
      reservationsVersion: 0,
    }
  },
  computed: {
    dateLabel(): string {
      const weekdays = ['日', '月', '火', '水', '木', '金', '土']
      const d = this.selectedDate
      return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日(${weekdays[d.getDay()]})`
    },
  },
  methods: {
    shiftDate(days: number) {
      const next = new Date(this.selectedDate)
      next.setDate(next.getDate() + days)
      this.selectedDate = next
    },
    setSelectedDate(date: Date) {
      this.selectedDate = date
    },
    onReservationCreated() {
      this.reservationsVersion += 1
      this.isCreateDialogOpen = false
    },
  },
})
</script>

<template>
  <div class="app-layout">
    <ReservationToolbar
      :date-label="dateLabel"
      :selected-date="selectedDate"
      @prev-date="shiftDate(-1)"
      @next-date="shiftDate(1)"
      @select-date="setSelectedDate"
      @open-create="isCreateDialogOpen = true"
    />
    <div class="app-body">
      <AppSidebar />
      <RouterView v-slot="{ Component }">
        <component :is="Component" :selected-date="selectedDate" :reservations-version="reservationsVersion" />
      </RouterView>
    </div>

    <ReservationCreateDialog
      v-if="isCreateDialogOpen"
      :selected-date="selectedDate"
      @close="isCreateDialogOpen = false"
      @created="onReservationCreated"
    />
  </div>
</template>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.app-body {
  display: flex;
  flex: 1;
  min-height: 0;
}
</style>
