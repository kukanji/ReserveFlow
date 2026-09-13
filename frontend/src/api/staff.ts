import { apiGet, apiPostJson, apiDelete } from './client'
import type { Staff } from '@/types/staff'

export function fetchStaffList(): Promise<Staff[]> {
  return apiGet<Staff[]>('/staffs')
}

export function createStaff(name: string): Promise<number> {
  return apiPostJson<number>('/staffs', { name })
}

export function deleteStaff(id: number): Promise<void> {
  return apiDelete(`/staffs/${id}`)
}
