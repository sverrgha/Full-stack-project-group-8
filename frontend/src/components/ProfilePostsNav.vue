<!-- ProfilePostsNav.vue - Navigation tabs for profile page content -->

<script setup>
import {  computed } from 'vue'

// Props with default to make parent component aware of active tab
const props = defineProps({
  modelValue: {
    type: String,
    default: 'posts'
  }
})

// Emits to update parent component when tab changes
const emit = defineEmits(['update:modelValue'])

// Method to change active tab
const setActiveTab = (tab) => {
  emit('update:modelValue', tab)
}
</script>

<template>
  <div class="tab-nav">
    <button
        :class="{ active: modelValue === 'posts' }"
        @click="setActiveTab('posts')"
    >
      <img src="../assets/grid.svg" class="icon" /> Posts
    </button>
    <button
        :class="{ active: modelValue === 'favorites' }"
        @click="setActiveTab('favorites')"
    >
      <img src="../assets/heart1.svg" class="icon" /> Favorites
    </button>
    <button
        :class="{ active: modelValue === 'purchased' }"
        @click="setActiveTab('purchased')"
    >
      <img src="../assets/purchased.svg" class="icon" /> Purchased
    </button>
  </div>
</template>

<style scoped>
.tab-nav {
  padding-bottom: 2rem;
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 2.5rem;
}

.tab-nav button {
  background: none;
  border: none;
  padding: 0.75rem 0;
  font-weight: bold;
  font-size: 1.2rem;
  color: #666;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.tab-nav button:hover {
  background-color: #d1d5db;
}

.tab-nav button.active {
  color: #4f46e5;
  border-bottom: 2px solid #4f46e5;
  font-weight: 600;
}

.tab-nav .icon {
  width: 25px;
  height: 25px;
  vertical-align: middle;
  padding-right: 1rem;
}

@media (max-width: 768px) {
  .tab-nav button {
    width: auto;
    font-size: 1rem;
  }
}
</style>