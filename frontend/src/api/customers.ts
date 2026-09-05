import { apiGet, apiPostJson } from './client'
import type { Customer, CustomerCreatePayload } from '@/types/customer'

export function searchCustomers(name: string): Promise<Customer[]> {
  return apiGet<Customer[]>(`/customers?name=${encodeURIComponent(name)}`)
}

export function createCustomer(payload: CustomerCreatePayload): Promise<number> {
  return apiPostJson<number>('/customers', payload)
}
