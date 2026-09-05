import { apiGet } from './client'
import type { Staff } from '@/types/staff'

export function fetchStaffList(): Promise<Staff[]> {
  return apiGet<Staff[]>('/staffs')
}
