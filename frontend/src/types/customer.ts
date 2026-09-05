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
