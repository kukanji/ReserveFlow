<script lang="ts">
import { defineComponent } from 'vue'
import { searchCustomers } from '@/api/customers'
import { fetchReservations } from '@/api/reservations'
import type { Customer } from '@/types/customer'
import type { Reservation } from '@/types/reservation'

const SEARCH_DEBOUNCE_MS = 300

export default defineComponent({
  name: 'CustomerSearchBox',
  emits: ['open-edit'],
  data() {
    return {
      query: '',
      isOpen: false,
      isSearching: false,
      hasSearched: false,
      results: [] as Customer[],
      selectedCustomer: null as Customer | null,
      upcomingReservations: [] as Reservation[],
      pastReservations: [] as Reservation[],
      isLoadingDetail: false,
      debounceTimer: null as ReturnType<typeof setTimeout> | null,
    }
  },
  mounted() {
    document.addEventListener('click', this.handleOutsideClick)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleOutsideClick)
    if (this.debounceTimer) clearTimeout(this.debounceTimer)
  },
  methods: {
    onQueryInput() {
      this.selectedCustomer = null
      if (this.debounceTimer) clearTimeout(this.debounceTimer)

      const trimmed = this.query.trim()
      if (trimmed === '') {
        this.isOpen = false
        this.results = []
        this.hasSearched = false
        return
      }

      this.isOpen = true
      this.debounceTimer = setTimeout(() => this.runSearch(trimmed), SEARCH_DEBOUNCE_MS)
    },
    async runSearch(query: string) {
      this.isSearching = true
      try {
        this.results = await searchCustomers(query)
        this.hasSearched = true
      } finally {
        this.isSearching = false
      }
    },
    async selectCandidate(customer: Customer) {
      this.isLoadingDetail = true
      try {
        const allReservations = await fetchReservations()
        this.selectedCustomer = customer
        const now = new Date()
        const customerReservations = allReservations.filter((r: Reservation) => r.customerId === customer.id)
        this.upcomingReservations = customerReservations
          .filter((r: Reservation) => new Date(r.startTime) >= now)
          .sort((a: Reservation, b: Reservation) => a.startTime.localeCompare(b.startTime))
        this.pastReservations = customerReservations
          .filter((r: Reservation) => new Date(r.startTime) < now)
          .sort((a: Reservation, b: Reservation) => b.startTime.localeCompare(a.startTime))
      } finally {
        this.isLoadingDetail = false
      }
    },
    backToResults() {
      this.selectedCustomer = null
      this.upcomingReservations = []
      this.pastReservations = []
    },
    formatDateTime(value: string): string {
      return `${value.slice(0, 10)} ${value.slice(11, 16)}`
    },
    openEdit(reservation: Reservation) {
      this.$emit('open-edit', reservation)
      this.isOpen = false
    },
    handleOutsideClick() {
      if (!this.isOpen) return
      this.isOpen = false
    },
  },
})
</script>

<template>
  <div class="customer-search-box" @click.stop>
    <input
      v-model="query"
      type="search"
      class="search-input"
      placeholder="顧客名で検索"
      @input="onQueryInput"
      @focus="onQueryInput"
    />

    <div v-if="isOpen" class="search-popover">
      <div v-if="selectedCustomer" class="detail-view">
        <button type="button" class="link-button" @click="backToResults">← 検索結果に戻る</button>
        <p class="detail-name">{{ selectedCustomer.name }}</p>
        <p class="detail-phone">{{ selectedCustomer.phoneNumber }}</p>

        <p v-if="isLoadingDetail" class="status-message">読み込み中...</p>

        <template v-else>
          <template v-if="upcomingReservations.length > 0">
            <p class="section-label">今後の予約</p>
            <ul class="upcoming-list">
              <li v-for="reservation in upcomingReservations" :key="reservation.id">
                <button type="button" class="upcoming-item" @click="openEdit(reservation)">
                  <span class="upcoming-datetime">{{ formatDateTime(reservation.startTime) }}</span>
                  <span>{{ reservation.menuName }}</span>
                  <span class="upcoming-staff">担当: {{ reservation.staffName }}</span>
                </button>
              </li>
            </ul>
          </template>

          <p class="section-label">来店履歴</p>
          <p v-if="pastReservations.length === 0" class="status-message">来店履歴はありません。</p>
          <ul v-else class="visit-history-list">
            <li v-for="reservation in pastReservations" :key="reservation.id">
              <button type="button" class="visit-history-item" @click="openEdit(reservation)">
                <div class="visit-history-main">
                  <span class="visit-history-date">{{ formatDateTime(reservation.startTime) }}</span>
                  <span class="visit-history-menu">{{ reservation.menuName }}</span>
                  <span class="visit-history-staff">担当: {{ reservation.staffName }}</span>
                </div>
                <p v-if="reservation.memo" class="visit-history-memo">{{ reservation.memo }}</p>
              </button>
            </li>
          </ul>
        </template>
      </div>

      <div v-else>
        <p v-if="isSearching" class="status-message">検索中...</p>
        <ul v-else-if="results.length > 0" class="search-results">
          <li v-for="customer in results" :key="customer.id">
            <button type="button" class="search-result" @click="selectCandidate(customer)">
              <span>{{ customer.name }}</span>
              <span class="search-result-phone">{{ customer.phoneNumber }}</span>
            </button>
          </li>
        </ul>
        <p v-else-if="hasSearched" class="status-message">該当する顧客が見つかりませんでした。</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.customer-search-box {
  position: relative;
}

.search-input {
  padding: 6px 12px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background: var(--color-background);
  color: var(--color-text);
  font-size: 25px;
}

.search-popover {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  width: 420px;
  max-height: 400px;
  overflow-y: auto;
  padding: 12px;
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.15);
  z-index: 20;
}

.status-message {
  font-size: 16px;
  color: var(--color-text-muted);
  padding: 4px 2px;
}

.search-results {
  display: flex;
  flex-direction: column;
  gap: 4px;
  list-style: none;
}

.section-label {
  font-size: 13px;
  font-weight: 700;
  color: var(--color-text-muted);
  margin: 10px 0 6px;
}

.upcoming-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  list-style: none;
}

.upcoming-item {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  width: 100%;
  padding: 8px 10px;
  border: 1px solid var(--color-primary);
  border-radius: 8px;
  background: var(--color-primary-soft);
  cursor: pointer;
  text-align: left;
  font-size: 14px;
  color: var(--color-primary);
}

.upcoming-item:hover {
  filter: brightness(0.96);
}

.upcoming-datetime {
  font-weight: 700;
}

.upcoming-staff {
  color: var(--color-primary);
  opacity: 0.85;
}

.search-result {
  display: flex;
  width: 100%;
  justify-content: space-between;
  padding: 10px 12px;
  border: none;
  border-radius: 8px;
  background: none;
  cursor: pointer;
  text-align: left;
  font-size: 16px;
}

.search-result:hover {
  background: var(--color-background-soft);
}

.search-result-phone {
  color: var(--color-text-muted);
}

.link-button {
  border: none;
  background: none;
  color: var(--color-primary);
  font-size: 14px;
  cursor: pointer;
  padding: 0;
  margin-bottom: 8px;
}

.detail-name {
  font-size: 18px;
  font-weight: 700;
}

.detail-phone {
  font-size: 14px;
  color: var(--color-text-muted);
  margin-bottom: 10px;
}

.visit-history-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  list-style: none;
}

.visit-history-item {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: var(--color-background-soft);
  cursor: pointer;
  text-align: left;
  font-family: inherit;
}

.visit-history-item:hover {
  background: var(--color-background-mute);
}

.visit-history-main {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 14px;
}

.visit-history-date {
  font-weight: 700;
}

.visit-history-staff {
  color: var(--color-text-muted);
}

.visit-history-memo {
  margin-top: 4px;
  font-size: 13px;
  color: var(--color-text-muted);
}
</style>
