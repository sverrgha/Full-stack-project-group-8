<script setup>
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
// This component is a button that toggles the visibility of a sidebar.
const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  }
});

// Emit event to parent component
const emit = defineEmits(['toggle']);


const toggleSidebar = () => {
  emit('toggle');
};
</script>

<template>
  <!-- Button to toggle sidebar visibility -->
  <button
      class="filter-button"
      :class="{ 'active': isOpen }"
      @click="toggleSidebar"
  >
    <img src="../assets/filters.svg" alt="Filter Icon" class="filter-icon" />
    <span class="filter-text">{{ t('productPage.filters')}}</span>
  </button>
</template>

<style scoped>
.filter-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: white;
  border: 1px solid #d1d5db;
  border-radius: 25px;
  padding: 8px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-button:hover {
  background-color: #f5f5f5;
}

.filter-button.active {
  background-color: #f0f7ff;
  border-color: #3b82f6;
}

.filter-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
}

.filter-text {
  font-size: 0.875rem;
  color: #374151;
}
</style>