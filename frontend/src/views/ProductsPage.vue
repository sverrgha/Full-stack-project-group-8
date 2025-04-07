<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useI18n } from 'vue-i18n';
import ItemGrid from '../components/ItemGrid.vue'
import CategoryButtons from "../components/products/CategoryButtons.vue";
import FilterButton from '../components/products/FilterButton.vue';
import FilterSidebar from '../components/products/FilterSidebar.vue';
import SelectField from '../components/form/SelectField.vue';
import BaseInputField from '../components/form/BaseInputField.vue';
import searchIcon from '/src/assets/SearchIcon.svg';

const { t } = useI18n();

// Mock product items
const products = [
  {
    id: 101,
    title: 'Leather Sofa',
    location: 'Oslo',
    price: 8500,
    imageUrl: 'https://placehold.co/300x300?text=Leather+Sofa'
  },
  {
    id: 102,
    title: 'Coffee Table',
    location: 'Bergen',
    price: 1800,
    imageUrl: 'https://placehold.co/300x300?text=Coffee+Table'
  },
  {
    id: 103,
    title: 'Dining Set',
    location: 'Trondheim',
    price: 5200,
    imageUrl: 'https://placehold.co/300x300?text=Dining+Set'
  },
  {
    id: 104,
    title: 'Bookshelf',
    location: 'Stavanger',
    price: 2100,
    imageUrl: 'https://placehold.co/300x300?text=Bookshelf'
  },
  {
    id: 105,
    title: 'Floor Lamp',
    location: 'Oslo',
    price: 950,
    imageUrl: 'https://placehold.co/300x300?text=Floor+Lamp'
  }
]

// State to track if sidebar is open
const isSidebarOpen = ref(false);

// Toggle sidebar visibility
const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value;

  // Prevent scrolling on body when sidebar is open
  if (isSidebarOpen.value) {
    document.body.style.overflow = 'hidden';
  } else {
    document.body.style.overflow = 'auto';
  }
};

// Close sidebar
const closeSidebar = () => {
  isSidebarOpen.value = false;
  document.body.style.overflow = 'auto';
};

// Search functionality moved from SearchBar.vue
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

// Sort functionality moved from Sort.vue
const selectedSort = ref('');

// Define the options for sorting
const sortOptions = computed(() => [
  { value: 'date_newest', label: t('sort.newest') },
  { value: 'date_oldest', label: t('sort.oldest') },
  { value: 'price_low', label: t('sort.priceLowToHigh') },
  { value: 'price_high', label: t('sort.priceHighToLow') }
]);

// Watch for changes to the selected option
const handleSortChange = (event) => {
  // You can emit an event here if needed
  console.log('Sort changed:', selectedSort.value);
};
</script>

<template>
  <div class="products-page">
    <!-- Filter Sidebar -->
    <FilterSidebar
        :isOpen="isSidebarOpen"
        @close="closeSidebar"
    />

    <div class="top-controls">
      <div class="searchbar">
        <!-- Search component incorporated directly -->
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
                <button @click="clearHistory" class="clear-history">Clear All</button>
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
      </div>

      <div class="filter-sort-controls">
        <FilterButton
            :isOpen="isSidebarOpen"
            @toggle="toggleSidebar"
        />
        <!-- Sort component incorporated directly -->
        <div class="sort-container">
          <SelectField
              id="product-sort"
              :label="t('productPage.sortBy')"
              v-model="selectedSort"
              :options="sortOptions"
              optionValueKey="value"
              optionLabelKey="label"
              :placeholder="t('productPage.sortBy')"
              class="sort-select"
              hideLabel
              @change="handleSortChange"
          />
        </div>
      </div>
    </div>

    <div class="category-buttons card">
      <CategoryButtons />
    </div>

    <div class="products-posts card">
      <ItemGrid :items="products" Class="grid-layout" />
    </div>
  </div>
</template>

<style scoped>
.products-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
  position: relative;
}

.top-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 2rem;
  margin-bottom: 2rem;
  height: 40px;
}

.searchbar {
  flex: 1;
  max-width: 500px;
  height: 100%;
}

.filter-sort-controls {
  display: flex;
  gap: 12px;
  align-items: center;
  height: 100%;
}

.category-buttons {
  margin-top: 2rem;
  margin-bottom: 2rem;
}

.products-posts {
  margin-top: 2rem;
  padding-bottom: 2rem;
}

.grid-layout {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
  gap: 3rem;
  transition: all 0.3s ease;
  min-height: 700px;
  justify-content: space-between;
  grid-auto-rows: minmax(min-content, max-content);
  width: 100%;
}

.sort-container {
  display: inline-block;
  height: 40px;
}

.sort-select :deep(select) {
  display: flex;
  align-items: center;
  background-color: #ffffff;
  border: 1px solid #d1d5db;
  border-radius: 25px;
  color: #374151;
  font-size: 1rem;
  line-height: 1.25rem;
  padding: 8px 16px 8px 16px;
  cursor: pointer;
  min-width: 150px;
  height: 40px;
  appearance: none;
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 0.8rem;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.sort-select :deep(select:hover) {
  background-color: #f9fafb;
}

.sort-select :deep(select:focus) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
  outline: none;
}

.sort-select :deep(.form-group) {
  margin-bottom: 0;
  height: 100%;
}

.sort-select :deep(.select-wrapper) {
  height: 100%;
}

.sort-select :deep(label) {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

.sort-select :deep(option) {
  padding: 0.75rem 1rem;
}

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

@media (max-width: 768px) {
  .top-controls {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .searchbar {
    max-width: 100%;
    width: 100%;
  }

  .filter-sort-controls {
    width: 100%;
    justify-content: space-between;
  }

  .grid-layout {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}
</style>