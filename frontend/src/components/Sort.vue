<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';

const isOpen = ref(false);
const selectedOption = ref({ value: '', label: 'Sort' });

const options = [
  { value: 'date_newest', label: 'Date (Newest first)' },
  { value: 'date_oldest', label: 'Date (Oldest first)' },
  { value: 'price_low', label: 'Price (Low to High)' },
  { value: 'price_high', label: 'Price (High to Low)' }
];

// Function to toggle the dropdown menu
const toggleDropdown = () => {
  isOpen.value = !isOpen.value;
};

// Function to select an option from the dropdown
const selectOption = (option) => {
  selectedOption.value = option;
  isOpen.value = false;
};

// Function to handle clicks outside the dropdown
const handleClickOutside = (event) => {
  const dropdown = document.querySelector('.sort-component');
  if (dropdown && !dropdown.contains(event.target)) {
    isOpen.value = false;
  }
};

// Add and remove event listeners for clicks outside
onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<template>
  <div class="sort-component">
    <!-- Custom dropdown toggle button -->
    <div
        class="sort-select"
        @click="toggleDropdown"
        :class="{ 'active': isOpen }"
    >
      <span>{{ selectedOption.label }}</span>
      <img
          src="../assets/dropdown-arrow.svg"
          alt="Dropdown arrow"
          class="arrow-icon"
          :class="{ 'rotate': isOpen }"
      />
    </div>

    <!-- Custom dropdown menu -->
    <div
        class="dropdown-menu"
        v-if="isOpen"
    >
      <div
          v-for="option in options"
          :key="option.value"
          class="dropdown-item"
          :class="{ 'selected': selectedOption.value === option.value }"
          @click="selectOption(option)"
      >
        {{ option.label }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.sort-component {
  position: relative;
  display: inline-block;
}

.sort-select {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #ffffff;
  border: 1px solid #d1d5db;
  border-radius: 25px;
  color: #374151;
  font-size: 1rem;
  line-height: 1.25rem;
  padding: 0.5rem 2rem 0.5rem 0.75rem;
  cursor: pointer;
  min-width: 150px;
  user-select: none;
  position: relative;
  transition: all 0.2s ease;
}

.sort-select.active {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}

.sort-select:hover {
  background-color: #f9fafb;
}

.arrow-icon {
  position: absolute;
  right: 0.75rem;
  width: 0.8rem;
  height: 0.8rem;
  transition: transform 0.2s ease;
}

.arrow-icon.rotate {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 5px);
  left: 0;
  z-index: 10;
  width: 100%;
  min-width: 150px;
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dropdown-item {
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: background-color 0.15s ease;
}

.dropdown-item:hover {
  background-color: #e0f2fe;
  color: #1d4ed8;
}

.dropdown-item.selected {
  background-color: #f0f7ff;
  color: #1d4ed8;
  font-weight: 500;
}

.dropdown-item:not(:last-child) {
  border-bottom: 1px solid #f3f4f6;
}
</style>