export interface Shift {
  id: number
  staffId: number
  staffName: string
  workDate: string
  off: boolean
  startTime: string | null
  endTime: string | null
  task: string | null
}

export interface ShiftDayEntry {
  workDate: string
  off: boolean
  startTime: string | null
  endTime: string | null
  task: string | null
}

export interface ShiftBulkUpsertPayload {
  staffId: number
  days: ShiftDayEntry[]
}
