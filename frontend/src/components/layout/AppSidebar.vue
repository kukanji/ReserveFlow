<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'AppSidebar',
})
</script>

<template>
  <aside class="sidebar">
    <nav class="sidebar-nav">
      <RouterLink to="/" class="sidebar-link">予約一覧</RouterLink>
      <span class="sidebar-link">スタッフ</span>
    </nav>
    <nav class="sidebar-nav">
      <span class="sidebar-link">ログアウト</span>
      <span class="sidebar-link">設定</span>
    </nav>
  </aside>
</template>

<style scoped>
.sidebar {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 200px;
  flex-shrink: 0;
  padding: 24px 16px;
  border-right: 1px solid var(--color-border);
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-link {
  font-size: 25px;
  font-weight: 700;
  color: var(--color-text);
  text-decoration: none;
  text-align: center;
  padding: 4px 8px;
  border-left: 3px solid transparent;
}

.sidebar-link.router-link-active {
  color: var(--color-primary);
  border-left-color: var(--color-primary);
}

.sidebar-link.is-disabled {
  color: var(--color-text-muted);
  cursor: default;
}
</style>
