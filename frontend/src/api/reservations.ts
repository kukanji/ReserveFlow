import { apiGet, apiPost } from './client'
import type { Reservation, ReservationCreatePayload } from '@/types/reservation'

export function fetchReservations(): Promise<Reservation[]> {
  return apiGet<Reservation[]>('/reservations')
}

export function createReservation(payload: ReservationCreatePayload): Promise<void> {
  return apiPost('/reservations', payload)
}
