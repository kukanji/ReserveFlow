<script lang="ts">
import { defineComponent, type PropType } from 'vue'
import { deleteStaff } from '@/api/staff'
import type { Staff } from '@/types/staff'

export default defineComponent({
  name: 'DeleteStaffDialog',
  props: {
    staff: {
      type: Object as PropType<Staff>,
      required: true,
    },
  },
  emits: ['close', 'deleted'],
  data() {
    return {
      isSubmitting: false,
      errorMessage: '',
    }
  },
  methods: {
    async confirmDelete() {
      if (this.isSubmitting) return
      this.isSubmitting = true
      this.errorMessage = ''
      try {
        await deleteStaff(this.staff.id)
        this.$emit('deleted')
      } catch {
        this.errorMessage = 'スタッフの削除に失敗しました。'
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
        <h2>スタッフを削除</h2>
        <button type="button" class="close-button" @click="$emit('close')" aria-label="閉じる">×</button>
      </div>
      <div class="dialog-body">
        <p class="warn-text">
          <span class="target-name">{{ staff.name }}</span> を削除しますか？<br /><br />
          このスタッフに登録済みの今後のシフトはすべて削除されます。過去のシフト実績は残ります。
        </p>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      </div>
      <div class="dialog-footer">
        <button type="button" class="cancel-button" @click="$emit('close')">キャンセル</button>
        <button type="button" class="danger-button" :disabled="isSubmitting" @click="confirmDelete">
          削除する
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
  width: 420px;
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
}

.warn-text {
  font-size: 15px;
  line-height: 1.7;
}

.target-name {
  font-weight: 700;
}

.error-message {
  margin-top: 10px;
  color: #d0342c;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 18px 22px;
  border-top: 1px solid var(--color-border);
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
  border: none;
  border-radius: 8px;
  background: #d1442f;
  color: white;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
}

.danger-button:disabled {
  opacity: 0.5;
  cursor: default;
}
</style>
