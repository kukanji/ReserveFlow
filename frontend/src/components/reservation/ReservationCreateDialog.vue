<script lang="ts">
import { defineComponent, type PropType } from 'vue'
import { fetchStaffList } from '@/api/staff'
import { fetchMenuList } from '@/api/menus'
import { searchCustomers, createCustomer } from '@/api/customers'
import { createReservation } from '@/api/reservations'
import type { Staff } from '@/types/staff'
import type { Menu } from '@/types/menu'
import type { Customer } from '@/types/customer'

function toDateInputValue(date: Date): string {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

export default defineComponent({
  name: 'ReservationCreateDialog',
  props: {
    selectedDate: {
      type: Date as PropType<Date>,
      required: true,
    },
  },
  emits: ['close', 'created'],
  data() {
    return {
      staffList: [] as Staff[],
      menuList: [] as Menu[],

      customerSearchQuery: '',
      customerSearchResults: [] as Customer[],
      hasSearched: false,
      isSearching: false,
      selectedCustomer: null as Customer | null,
      isCreatingNewCustomer: false,
      newCustomerName: '',
      newCustomerPhone: '',

      staffId: null as number | null,
      menuId: null as number | null,
      reservationDate: toDateInputValue(this.selectedDate),
      reservationTime: '10:00',
      memo: '',

      isSubmitting: false,
      errorMessage: '',
    }
  },
  computed: {
    canSubmit(): boolean {
      const hasCustomer =
        this.selectedCustomer !== null ||
        (this.isCreatingNewCustomer && this.newCustomerName.trim() !== '' && this.newCustomerPhone.trim() !== '')
      return (
        hasCustomer &&
        this.staffId !== null &&
        this.menuId !== null &&
        this.reservationDate !== '' &&
        this.reservationTime !== ''
      )
    },
  },
  async mounted() {
    const [staffData, menuData] = await Promise.all([fetchStaffList(), fetchMenuList()])
    this.staffList = staffData
    this.menuList = menuData
  },
  methods: {
    async runCustomerSearch() {
      if (this.customerSearchQuery.trim() === '') return
      this.isSearching = true
      try {
        this.customerSearchResults = await searchCustomers(this.customerSearchQuery.trim())
        this.hasSearched = true
      } finally {
        this.isSearching = false
      }
    },
    selectCustomer(customer: Customer) {
      this.selectedCustomer = customer
      this.customerSearchResults = []
    },
    changeCustomer() {
      this.selectedCustomer = null
      this.customerSearchQuery = ''
      this.customerSearchResults = []
      this.hasSearched = false
    },
    startNewCustomer() {
      this.isCreatingNewCustomer = true
    },
    cancelNewCustomer() {
      this.isCreatingNewCustomer = false
      this.newCustomerName = ''
      this.newCustomerPhone = ''
    },
    async submit() {
      if (!this.canSubmit) return
      this.isSubmitting = true
      this.errorMessage = ''
      try {
        let customerId: number
        if (this.selectedCustomer) {
          customerId = this.selectedCustomer.id
        } else {
          customerId = await createCustomer({
            name: this.newCustomerName.trim(),
            phoneNumber: this.newCustomerPhone.trim(),
            memo: null,
          })
        }

        await createReservation({
          customerId,
          staffId: this.staffId as number,
          menuId: this.menuId as number,
          startTime: `${this.reservationDate}T${this.reservationTime}:00`,
          memo: this.memo.trim() === '' ? null : this.memo.trim(),
        })

        this.$emit('created')
      } catch {
        this.errorMessage = '予約の作成に失敗しました。入力内容や時間の重複を確認してください。'
      } finally {
        this.isSubmitting = false
      }
    },
  },
})
</script>

<template>
  <div class="overlay" @click.self="$emit('close')">
    <div class="dialog">
      <div class="dialog-header">
        <h2>予約作成</h2>
        <button type="button" class="close-button" @click="$emit('close')" aria-label="閉じる">×</button>
      </div>

      <div class="dialog-body">
        <section class="field-group">
          <h3 class="field-group-title">顧客</h3>

          <div v-if="selectedCustomer" class="selected-customer">
            <div>
              <p class="selected-customer-name">{{ selectedCustomer.name }}</p>
              <p class="selected-customer-phone">{{ selectedCustomer.phoneNumber }}</p>
            </div>
            <button type="button" class="link-button" @click="changeCustomer">変更</button>
          </div>

          <div v-else-if="isCreatingNewCustomer" class="new-customer-form">
            <label class="field">
              <span class="field-label">名前</span>
              <input v-model="newCustomerName" type="text" placeholder="山田 太郎" />
            </label>
            <label class="field">
              <span class="field-label">電話番号</span>
              <input v-model="newCustomerPhone" type="tel" placeholder="090-0000-0000" />
            </label>
            <button type="button" class="link-button" @click="cancelNewCustomer">検索に戻る</button>
          </div>

          <div v-else class="customer-search">
            <div class="search-row">
              <input
                v-model="customerSearchQuery"
                type="text"
                placeholder="顧客名で検索"
                @keyup.enter="runCustomerSearch"
              />
              <button type="button" @click="runCustomerSearch" :disabled="isSearching">検索</button>
            </div>

            <ul v-if="customerSearchResults.length > 0" class="search-results">
              <li v-for="customer in customerSearchResults" :key="customer.id">
                <button type="button" class="search-result" @click="selectCustomer(customer)">
                  <span>{{ customer.name }}</span>
                  <span class="search-result-phone">{{ customer.phoneNumber }}</span>
                </button>
              </li>
            </ul>
            <p v-else-if="hasSearched" class="no-results">該当する顧客が見つかりませんでした。</p>

            <button type="button" class="link-button" @click="startNewCustomer">新規顧客として登録</button>
          </div>
        </section>

        <section class="field-group">
          <h3 class="field-group-title">予約内容</h3>
          <label class="field">
            <span class="field-label">スタッフ</span>
            <select v-model.number="staffId">
              <option :value="null" disabled>選択してください</option>
              <option v-for="staff in staffList" :key="staff.id" :value="staff.id">{{ staff.name }}</option>
            </select>
          </label>
          <label class="field">
            <span class="field-label">メニュー</span>
            <select v-model.number="menuId">
              <option :value="null" disabled>選択してください</option>
              <option v-for="menu in menuList" :key="menu.id" :value="menu.id">
                {{ menu.name }}（{{ menu.durationMinutes }}分）
              </option>
            </select>
          </label>
          <div class="field-row">
            <label class="field">
              <span class="field-label">日付</span>
              <input v-model="reservationDate" type="date" />
            </label>
            <label class="field">
              <span class="field-label">開始時刻</span>
              <input v-model="reservationTime" type="time" />
            </label>
          </div>
          <label class="field">
            <span class="field-label">メモ</span>
            <textarea v-model="memo" rows="2" placeholder="ご要望など"></textarea>
          </label>
        </section>

        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      </div>

      <div class="dialog-footer">
        <button type="button" class="cancel-button" @click="$emit('close')">キャンセル</button>
        <button type="button" class="submit-button" :disabled="!canSubmit || isSubmitting" @click="submit">
          作成
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.dialog {
  width: 480px;
  max-height: 90vh;
  overflow-y: auto;
  background: var(--color-background);
  border-radius: 12px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.2);
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--color-border);
}

.dialog-header h2 {
  font-size: 18px;
  font-weight: 700;
}

.close-button {
  width: 28px;
  height: 28px;
  border: none;
  background: none;
  font-size: 20px;
  color: var(--color-text-muted);
  cursor: pointer;
}

.dialog-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.field-group-title {
  font-size: 13px;
  font-weight: 700;
  color: var(--color-text-muted);
}

.field {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.field-label {
  font-size: 12px;
  color: var(--color-text-muted);
}

.field-row {
  display: flex;
  gap: 12px;
}

.field-row .field {
  flex: 1;
}

input,
select,
textarea {
  padding: 8px 10px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background: var(--color-background);
  color: var(--color-text);
  font-size: 14px;
  font-family: inherit;
}

.search-row {
  display: flex;
  gap: 8px;
}

.search-row input {
  flex: 1;
}

.search-row button {
  padding: 8px 14px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background: var(--color-background);
  cursor: pointer;
}

.search-results {
  display: flex;
  flex-direction: column;
  list-style: none;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  overflow: hidden;
}

.search-result {
  display: flex;
  width: 100%;
  justify-content: space-between;
  padding: 8px 10px;
  border: none;
  background: none;
  cursor: pointer;
  text-align: left;
  font-size: 14px;
}

.search-result:hover {
  background: var(--color-background-soft);
}

.search-result-phone {
  color: var(--color-text-muted);
}

.no-results {
  font-size: 13px;
  color: var(--color-text-muted);
}

.link-button {
  align-self: flex-start;
  border: none;
  background: none;
  color: var(--color-primary);
  font-size: 13px;
  cursor: pointer;
  padding: 0;
}

.selected-customer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
}

.selected-customer-name {
  font-weight: 700;
}

.selected-customer-phone {
  font-size: 12px;
  color: var(--color-text-muted);
}

.new-customer-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.error-message {
  color: #d0342c;
  font-size: 13px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 16px 20px;
  border-top: 1px solid var(--color-border);
}

.cancel-button {
  padding: 8px 16px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background: var(--color-background);
  cursor: pointer;
}

.submit-button {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background: var(--color-primary);
  color: white;
  font-weight: 700;
  cursor: pointer;
}

.submit-button:disabled {
  opacity: 0.5;
  cursor: default;
}
</style>
