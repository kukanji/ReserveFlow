export interface Reservation {
  id: number
  customerId: number
  customerName: string
  staffId: number
  staffName: string
  menuId: number
  menuName: string
  startTime: string
  endTime: string
}

export interface ReservationCreatePayload {
  customerId: number
  staffId: number
  menuId: number
  startTime: string
  memo: string | null
}
