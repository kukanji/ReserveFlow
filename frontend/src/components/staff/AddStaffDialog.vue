<script lang="ts">
import { defineComponent } from 'vue'
import { createStaff } from '@/api/staff'

export default defineComponent({
  name: 'AddStaffDialog',
  emits: ['close', 'created'],
  data() {
    return {
      name: '',
      isSubmitting: false,
      errorMessage: '',
    }
  },
  computed: {
    canSubmit(): boolean {
      return this.name.trim() !== ''
    },
  },
  methods: {
    onNameKeydown(event: KeyboardEvent) {
      // isComposing/keyCode 229: IME変換確定のEnterを送信と区別するため
      if (event.isComposing || event.keyCode === 229) return
      this.submit()
    },
    async submit() {
      if (!this.canSubmit || this.isSubmitting) return
      this.isSubmitting = true
      this.errorMessage = ''
      try {
        await createStaff(this.name.trim())
        this.$emit('created')
      } catch {
        this.errorMessage = 'スタッフの追加に失敗しました。'
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
        <h2>スタッフを追加</h2>
        <button type="button" class="close-button" @click="$emit('close')" aria-label="閉じる">×</button>
      </div>
      <div class="dialog-body">
        <label class="field">
          <span class="field-label">名前</span>
          <input v-model="name" type="text" placeholder="例：渡辺 三郎" @keydown.enter="onNameKeydown" />
        </label>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      </div>
      <div class="dialog-footer">
        <button type="button" class="cancel-button" @click="$emit('close')">キャンセル</button>
        <button type="button" class="submit-button" :disabled="!canSubmit || isSubmitting" @click="submit">
          追加する
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
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 14px;
  color: var(--color-text-muted);
}

.field input {
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: var(--color-background);
  color: var(--color-text);
  font-size: 16px;
  font-family: inherit;
}

.error-message {
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
