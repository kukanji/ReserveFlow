import { apiGet } from './client'
import type { Menu } from '@/types/menu'

export function fetchMenuList(): Promise<Menu[]> {
  return apiGet<Menu[]>('/menus')
}
