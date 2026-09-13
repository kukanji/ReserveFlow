import { apiGet, apiPost, apiDelete } from './client'
import type { Shift, ShiftBulkUpsertPayload } from '@/types/shift'

export function fetchShifts(start: string, end: string): Promise<Shift[]> {
  return apiGet<Shift[]>(`/shifts?start=${start}&end=${end}`)
}

export function bulkUpsertShifts(payload: ShiftBulkUpsertPayload): Promise<void> {
  return apiPost('/shifts/bulk', payload)
}

export function deleteShift(id: number): Promise<void> {
  return apiDelete(`/shifts/${id}`)
}
