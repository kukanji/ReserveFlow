<script lang="ts">
import { defineComponent, type PropType } from 'vue'
import { bulkUpsertShifts, deleteShift } from '@/api/shifts'
import type { Staff } from '@/types/staff'
import type { Shift } from '@/types/shift'
import { SCHEDULE_START_HOUR, SCHEDULE_END_HOUR, SCHEDULE_SLOT_MINUTES } from '@/constants/schedule'

const WEEKDAYS = ['日', '月', '火', '水', '木', '金', '土']

function buildTimeOptions(): string[] {
  const options: string[] = []
  const totalMinutes = (SCHEDULE_END_HOUR - SCHEDULE_START_HOUR) * 60
  for (let minutes = 0; minutes <= totalMinutes; minutes += SCHEDULE_SLOT_MINUTES) {
    const hour = SCHEDULE_START_HOUR + Math.floor(minutes / 60)
    const minute = minutes % 60
    options.push(`${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`)
  }
  return options
}

export default defineComponent({
  name: 'ShiftDayDialog',
  props: {
    staffList: {
      type: Array as PropType<Staff[]>,
      required: true,
    },
    staffId: {
      type: Number,
      required: true,
    },
    dateKey: {
      type: String,
      required: true,
    },
    existing: {
      type: Object as PropType<Shift | null>,
      default: null,
    },
  },
  emits: ['close', 'saved'],
  data() {
    return {
      isOff: this.existing?.off ?? false,
      startTime: this.existing?.startTime?.slice(0, 5) ?? '09:00',
      endTime: this.existing?.endTime?.slice(0, 5) ?? '18:00',
      task: this.existing?.task ?? '',
      isSubmitting: false,
      errorMessage: '',
    }
  },
  computed: {
    timeOptions(): string[] {
      return buildTimeOptions()
    },
    staffName(): string {
      return this.staffList.find((s) => s.id === this.staffId)?.name ?? ''
    },
    dateLabel(): string {
      const parts = this.dateKey.split('-')
      const year = Number(parts[0])
      const month = Number(parts[1])
      const day = Number(parts[2])
      const date = new Date(year, month - 1, day)
      return `${year}年${month}月${day}日（${WEEKDAYS[date.getDay()]}）`
    },
  },
  methods: {
    async submit() {
      if (this.isSubmitting) return
      this.isSubmitting = true
      this.errorMessage = ''
      try {
        await bulkUpsertShifts({
          staffId: this.staffId,
          days: [
            {
              workDate: this.dateKey,
              off: this.isOff,
              startTime: this.isOff ? null : this.startTime,
              endTime: this.isOff ? null : this.endTime,
              task: this.isOff ? null : this.task.trim() === '' ? null : this.task.trim(),
            },
          ],
        })
        this.$emit('saved')
      } catch {
        this.errorMessage = '保存に失敗しました。'
      } finally {
        this.isSubmitting = false
      }
    },
    async removeShift() {
      if (this.isSubmitting || !this.existing) return
      this.isSubmitting = true
      this.errorMessage = ''
      try {
        await deleteShift(this.existing.id)
        this.$emit('saved')
      } catch {
        this.errorMessage = '削除に失敗しました。'
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
        <h2>{{ existing ? 'シフト編集' : 'シフトを追加' }}</h2>
        <button type="button" class="close-button" @click="$emit('close')" aria-label="閉じる">×</button>
      </div>
      <div class="dialog-body">
        <p class="context-line">
          <span class="context-staff">{{ staffName }}</span>
          <span class="context-date">{{ dateLabel }}</span>
        </p>

        <label class="off-toggle">
          <input type="checkbox" v-model="isOff" />
          <span>休みにする</span>
        </label>

        <div class="field-row">
          <label class="field">
            <span class="field-label">開始</span>
            <select v-model="startTime" :disabled="isOff">
              <option v-for="time in timeOptions" :key="time" :value="time">{{ time }}</option>
            </select>
          </label>
          <label class="field">
            <span class="field-label">終了</span>
            <select v-model="endTime" :disabled="isOff">
              <option v-for="time in timeOptions" :key="time" :value="time">{{ time }}</option>
            </select>
          </label>
        </div>

        <label class="field">
          <span class="field-label">担当業務（任意）</span>
          <input v-model="task" type="text" placeholder="例：レジ対応" :disabled="isOff" />
        </label>

        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      </div>
      <div class="dialog-footer">
        <button v-if="existing" type="button" class="danger-button" :disabled="isSubmitting" @click="removeShift">
          削除する
        </button>
        <div class="footer-spacer"></div>
        <button type="button" class="cancel-button" @click="$emit('close')">キャンセル</button>
        <button type="button" class="submit-button" :disabled="isSubmitting" @click="submit">保存する</button>
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
  width: 440px;
  background: var(--color-background);
  border-radius: 14px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.2);
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px;
  border-bottom: 1px solid var(--color-border);
}

.dialog-header h2 {
  font-size: 20px;
  font-weight: 700;
}

.close-button {
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  font-size: 22px;
  color: var(--color-text-muted);
  cursor: pointer;
}

.dialog-body {
  padding: 22px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.context-line {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.context-staff {
  font-size: 17px;
  font-weight: 700;
}

.context-date {
  font-size: 14px;
  color: var(--color-text-muted);
}

.off-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  cursor: pointer;
}

.field-row {
  display: flex;
  gap: 14px;
}

.field-row .field {
  flex: 1;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 13px;
  color: var(--color-text-muted);
}

input,
select {
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: var(--color-background);
  color: var(--color-text);
  font-size: 15px;
  font-family: inherit;
}

input:disabled,
select:disabled {
  background: var(--color-background-mute);
  color: var(--color-text-muted);
}

.error-message {
  color: #d0342c;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 18px 22px;
  border-top: 1px solid var(--color-border);
}

.footer-spacer {
  flex: 1;
}

.cancel-button {
  padding: 10px 18px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: var(--color-background);
  font-size: 15px;
  cursor: pointer;
}

.danger-button {
  padding: 10px 18px;
  border: 1px solid #d1442f;
  border-radius: 8px;
  background: var(--color-background);
  color: #d1442f;
  font-size: 15px;
  cursor: pointer;
}

.danger-button:disabled {
  opacity: 0.5;
  cursor: default;
}

.submit-button {
  padding: 10px 18px;
  border: none;
  border-radius: 8px;
  background: var(--color-primary);
  color: white;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
}

.submit-button:disabled {
  opacity: 0.5;
  cursor: default;
}
</style>
