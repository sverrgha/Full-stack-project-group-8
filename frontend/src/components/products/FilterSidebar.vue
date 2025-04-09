<script setup>
import { ref, computed } from 'vue';
import { useI18n } from "vue-i18n";

const { t } = useI18n();

// Define component props with a reactive `isOpen` property, defaulting to `false`
const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  }
});

// Emit event to parent component
const emit = defineEmits(['close', 'apply']);

// Function to close the sidebar
const closeSidebar = () => {
  emit('close');
};

// Price range
const priceMin = ref(0);
const priceMax = ref(100000);
const priceError = ref('');

// Computed property to check if price range is valid
const isPriceRangeValid = computed(() => {
  // Convert to numbers to ensure proper comparison
  const min = Number(priceMin.value);
  const max = Number(priceMax.value);

  // Now we only validate that min <= max since negative values are prevented via HTML
  if (min > max) {
    priceError.value = 'Minimum price cannot be higher than maximum price';
    return false;
  } else {
    priceError.value = '';
    return true;
  }
});

// Function to update min price with validation
const updateMinPrice = (event) => {
  let value = event.target.value;

  // Set to 0 if value is null
  if (value === null) {
    value = 0;
  }

  value = Number(value);

  // Simply reset to 0 if negative (should be prevented by HTML, but just in case)
  if (value < 0) {
    priceMin.value = 0;
    return;
  }

  priceMin.value = value;
};

// Function to update max price with validation
const updateMaxPrice = (event) => {
  let value = event.target.value;

  // Set to 0 if value is null
  if (value === null) {
    value = 0;
  }

  value = Number(value);

  // Simply reset to 0 if negative (should be prevented by HTML, but just in case)
  if (value < 0) {
    priceMax.value = 0;
    return;
  }

  priceMax.value = value;
};

// Store a single selected city value
const selectedCity = ref('');
const cities = ref([
  { id: 1, name: 'Oslo' },
  { id: 2, name: 'Bergen' },
  { id: 3, name: 'Trondheim' },
  { id: 4, name: 'Stavanger' },
  { id: 5, name: 'Tromsø' },
]);

// Condition options as an array of objects with checked property for multi-select
const conditions = ref([
  { id: 1, name: 'new', checked: false },
  { id: 2, name: 'like_new', checked: false },
  { id: 3, name: 'good', checked: false },
  { id: 4, name: 'fair', checked: false },
  { id: 5, name: 'poor', checked: false },
]);

// Computed property for selected conditions
const selectedConditions = computed(() => {
  return conditions.value.filter(cond => cond.checked).map(cond => cond.name);
});

const applyFilters = () => {
  if (!isPriceRangeValid.value) return;

  const activeFilters = {
    priceRange: { min: priceMin.value, max: priceMax.value },
    city: selectedCity.value, // Single city value instead of array
    conditions: selectedConditions.value
  };

  console.log('Applied filters:', activeFilters);
  emit('apply', activeFilters);

  if (window.innerWidth < 768) closeSidebar();
};

// Update resetFilters to clear the selected city
const resetFilters = () => {
  priceMin.value = 0;
  priceMax.value = 100000;
  priceError.value = '';

  selectedCity.value = ''; // Reset selected city
  conditions.value.forEach(cond => cond.checked = false);
};
</script>

<template>
  <div
      class="sidebar-overlay"
      :class="{ 'active': isOpen }"
      @click="closeSidebar"
  ></div>

  <aside class="filter-sidebar" :class="{ 'open': isOpen }">
    <div class="sidebar-header">
      <h2>{{ t('productPage.filters') }}</h2>
      <button class="close-button" @click="closeSidebar">
        <span>×</span>
      </button>
    </div>

    <div class="sidebar-content">

      <!-- Price Range -->
      <div class="filter-section">
        <h3>{{ t('productPageHidden.price') }}</h3>
        <div class="price-inputs">
          <div class="input-group">
            <label for="price-min">Min</label>
            <input
                type="number"
                id="price-min"
                :value="priceMin"
                @input="updateMinPrice"
                min="0"
                :class="{ 'input-error': !isPriceRangeValid }"
                placeholder="Min"
                @keydown="(event) => {
                  const allowedKeys = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab'];
                  if (!allowedKeys.includes(event.key)) {
                    event.preventDefault();
                  }
                }"
            />
          </div>
          <div class="input-group">
            <label for="price-max">Max</label>
            <input
                type="number"
                id="price-max"
                :value="priceMax"
                @input="updateMaxPrice"
                min="0"
                :class="{ 'input-error': !isPriceRangeValid }"
                placeholder="Max"
                @keydown="(event) => {
                  const allowedKeys = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab'];
                  if (!allowedKeys.includes(event.key)) {
                    event.preventDefault();
                  }
                }"
            />
          </div>
        </div>
        <div v-if="priceError" class="error-message">
          {{ priceError }}
        </div>
      </div>

      <div class="filter-section">
        <h3>{{ t('productPageHidden.city') }}</h3>
        <div class="checkbox-group">
          <div
              v-for="city in cities"
              :key="`city-${city.id}`"
              class="checkbox-item"
          >
            <input
                type="radio"
                :id="`city-${city.id}`"
                v-model="selectedCity"
                :value="city.name"
                name="city"
            />
            <label :for="`city-${city.id}`">{{ city.name }}</label>
          </div>
        </div>
      </div>

      <!-- Condition with checkboxes for multi-select -->
      <div class="filter-section">
        <h3>{{ t('productPageHidden.condition') }}</h3>
        <div class="checkbox-group">
          <div
              v-for="condition in conditions"
              :key="`cond-${condition.id}`"
              class="checkbox-item"
          >
            <input
                type="checkbox"
                :id="`cond-${condition.id}`"
                v-model="condition.checked"
            />
            <label :for="`cond-${condition.id}`">{{ t(`conditions.${condition.name}`) }}</label>
          </div>
        </div>
      </div>
    </div>

    <div class="sidebar-footer">
      <button class="reset-button" @click="resetFilters">{{ t('productPageHidden.clear') }}</button>
      <button
          class="apply-button"
          @click="applyFilters"
          :disabled="!isPriceRangeValid"
          :class="{ 'button-disabled': !isPriceRangeValid }"
      >
        {{ t('productPageHidden.apply') }}
      </button>
    </div>
  </aside>
</template>

<style scoped>
.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 998;
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.3s ease;
}

.sidebar-overlay.active {
  opacity: 1;
  visibility: visible;
}

.filter-sidebar {
  position: fixed;
  top: 0;
  left: -320px;
  width: 300px;
  height: 100%;
  background-color: white;
  z-index: 999;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  transition: left 0.3s ease;
  display: flex;
  flex-direction: column;
}

.filter-sidebar.open {
  left: 0;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
}

.sidebar-header h2 {
  margin: 0.9rem;
  font-size: 1.25rem;
  color: #111827;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: 50%;
}

.close-button:hover {
  background-color: #f3f4f6;
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.filter-section {
  margin-bottom: 24px;
  padding-right: 30px;
  position: relative;
}

.filter-section h3 {
  margin: 0 0 12px;
  font-size: 1rem;
  color: #374151;
}

.price-inputs {
  display: flex;
  gap: 10px;
  width: 100%;
  padding: 0;
  margin: 0 auto;
}

.input-group {
  flex: 1;
  width: 50%;
}

.input-group label {
  display: block;
  margin-bottom: 4px;
  font-size: 0.875rem;
  color: #6b7280;
}

.input-group input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 0.875rem;
}

.input-error {
  border-color: #ef4444 !important;
  background-color: #fef2f2;
}

.error-message {
  margin-top: 8px;
  padding: 8px 12px;
  background-color: #fef2f2;
  color: #dc2626;
  border-radius: 4px;
  font-size: 0.9rem;
  border-left: 3px solid #ef4444;
}

.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.checkbox-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.checkbox-item input[type="checkbox"] {
  margin: 0;
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.checkbox-item label {
  font-size: 0.875rem;
  color: #374151;
  cursor: pointer;
}

.sidebar-footer {
  padding: 16px 20px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  gap: 10px;
}

.reset-button, .apply-button {
  flex: 1;
  padding: 10px;
  border-radius: 6px;
  font-size: 0.875rem;
  cursor: pointer;
  text-align: center;
  transition: all 0.2s ease;
}

.reset-button {
  background-color: white;
  border: 1px solid #d1d5db;
  color: #4b5563;
}

.reset-button:hover {
  background-color: #f3f4f6;
}

.apply-button {
  background-color: #3b82f6;
  border: 1px solid #3b82f6;
  color: white;
}

.apply-button:hover:not(:disabled) {
  background-color: #2563eb;
}

.button-disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background-color: #9ca3af;
  border-color: #9ca3af;
}

@media (max-width: 768px) {
  .filter-sidebar {
    width: 280px;
  }
}
</style>