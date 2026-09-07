import { apiGet, apiPostJson } from './client'
import type { Customer, CustomerCreatePayload, CustomerDetail } from '@/types/customer'

export function searchCustomers(name: string): Promise<Customer[]> {
  return apiGet<Customer[]>(`/customers?name=${encodeURIComponent(name)}`)
}

export function fetchCustomerDetail(id: number): Promise<CustomerDetail> {
  return apiGet<CustomerDetail>(`/customers/${id}`)
}

export function createCustomer(payload: CustomerCreatePayload): Promise<number> {
  return apiPostJson<number>('/customers', payload)
}
