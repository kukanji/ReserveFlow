import { createRouter, createWebHistory } from 'vue-router'
import ReservationListView from '../views/ReservationListView.vue'
import StaffShiftView from '../views/StaffShiftView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'reservations',
      component: ReservationListView,
    },
    {
      path: '/staff',
      name: 'staff',
      component: StaffShiftView,
    },
  ],
})

export default router
