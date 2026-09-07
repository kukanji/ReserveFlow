export interface Customer {
  id: number
  name: string
  phoneNumber: string
}

export interface CustomerCreatePayload {
  name: string
  phoneNumber: string
  memo: string | null
}

export interface TreatmentHistory {
  visitDate: string
  menuName: string
  staffName: string
  memo: string | null
}

export interface CustomerDetail {
  id: number
  name: string
  phoneNumber: string
  memo: string | null
  lastTreatment: TreatmentHistory | null
  visitHistories: TreatmentHistory[]
}
