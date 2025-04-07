<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useI18n } from 'vue-i18n';
import BaseInputField from '../form/BaseInputField.vue';
import searchIcon from '/src/assets/SearchIcon.svg';

const { t } = useI18n();

// Define refs with proper types
const searchQuery = ref('');
const searchHistory = ref([]);
const showHistory = ref(false);
const inputElement = ref(null);
const searchContainer = ref(null);

// Load search history from localStorage when component mounts
onMounted(() => {
  const savedHistory = localStorage.getItem('searchHistory');
  if (savedHistory) {
    try {
      const parsed = JSON.parse(savedHistory);
      if (Array.isArray(parsed)) {
        searchHistory.value = parsed;
      }
    } catch (e) {
      console.error('Failed to parse search history:', e);
    }
  }

  // Add click listener to detect clicks outside the component
  document.addEventListener('mousedown', handleOutsideClick);
});

// Remove event listener when component is unmounted
onBeforeUnmount(() => {
  document.removeEventListener('mousedown', handleOutsideClick);
});

// Close dropdown when clicking outside
const handleOutsideClick = (event) => {
  if (searchContainer.value && !searchContainer.value.contains(event.target)) {
    showHistory.value = false;
  }
};

// Save search to history
const saveSearch = () => {
  const query = searchQuery.value;
  if (!query || query.trim() === '') return;

  // Remove the query if it already exists to avoid duplicates
  const index = searchHistory.value.indexOf(query);
  if (index !== -1) {
    searchHistory.value.splice(index, 1);
  }

  // Add new query to the beginning of the array
  searchHistory.value.unshift(query);

  // Limit history to 5 items
  if (searchHistory.value.length > 5) {
    searchHistory.value = searchHistory.value.slice(0, 5);
  }

  // Save to localStorage
  localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value));

  // Hide history after search
  showHistory.value = false;
};

// Set the input value to the selected history item
const selectHistoryItem = (item) => {
  if (typeof item === 'string') {
    searchQuery.value = item;
    showHistory.value = false;
    if (inputElement.value) {
      inputElement.value.$el.querySelector('input').focus();
    }
  }
};

// Clear all search history
const clearHistory = (event) => {
  if (event) {
    event.stopPropagation();
  }
  searchHistory.value = [];
  localStorage.removeItem('searchHistory');
  showHistory.value = false;
};

// Shows history when input is focused
const handleFocus = () => {
  showHistory.value = true;
};

// Handle form submission
const handleSubmit = (e) => {
  e.preventDefault();
  saveSearch();
  // Remove focus from input element after submission
  if (inputElement.value) {
    inputElement.value.$el.querySelector('input').blur();
  }
  // Here you would also handle the actual search functionality
};
</script>

<template>
  <div>
    <!-- Background overlay -->
    <div
        class="search-overlay"
        :class="{ 'active': showHistory }"
        @click="showHistory = false"
    ></div>

    <div ref="searchContainer" class="search-input-container">
      <!-- Search Input -->
      <form @submit="handleSubmit" class="search-input">
        <BaseInputField
            ref="inputElement"
            v-model="searchQuery"
            id="product-search"
            :label="t('productPage.search')"
            :placeholder="t('productPage.search')"
            hideLabel
            @focus="handleFocus"
            class="search-field"
            :iconSrc="searchIcon"
            :iconAlt="'Search icon'"
        />
      </form>

      <!-- Search History Dropdown -->
      <div v-if="showHistory && searchHistory.length > 0" class="search-history">
        <div class="history-header">
          <button @click="clearHistory" class="clear-history">{{ t('productPageHidden.clear') }}</button>
        </div>
        <ul>
          <li
              v-for="(item, index) in searchHistory"
              :key="index"
              @mousedown.prevent="selectHistoryItem(item)"
              class="history-item"
          >
            <span>{{ item }}</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<style scoped>
.search-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 100;
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.3s ease;
}

.search-overlay.active {
  opacity: 1;
  visibility: visible;
}

.search-input-container {
  position: relative;
  z-index: 200;
}

.search-input {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.search-field {
  width: 100%;
}

.search-field :deep(input) {
  border-radius: 25px;
}

.search-field :deep(.form-group) {
  margin-bottom: 0;
}

.search-history {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  background: white;
  border: 1px solid #ddd;
  border-radius: 15px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 10;
  margin-top: -22px;
}

.history-header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 10px 15px;
  border-bottom: 1px solid #eee;
}

.clear-history {
  background: none;
  border: none;
  color: #4285f4;
  font-size: 14px;
  cursor: pointer;
}

.search-history ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  cursor: pointer;
}

.history-item:hover {
  background-color: #f5f5f5;
}
</style>