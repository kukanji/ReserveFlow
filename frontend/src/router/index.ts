import { createRouter, createWebHistory } from 'vue-router'
import ReservationListView from '../views/ReservationListView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'reservations',
      component: ReservationListView,
    },
  ],
})

export default router
