<!-- CategoryButtons.vue -->
<script setup>
import { ref, onMounted, watch } from 'vue';
import RecommendedButton from '../RecommendedButton.vue'; // Fixed import path
import { useI18n } from 'vue-i18n';
import { categoriesService } from '../../services/categoriesService';
import { useListingStore } from '../../stores/listing';

const { t, locale } = useI18n();
const emit = defineEmits(['select-category']);
const listingStore = useListingStore();

const categories = ref([]);
const selectedCategory = ref(null);
const isLoading = ref(true);
const error = ref(null);
const isRecommendedActive = ref(false);

// Fetch categories from the backend API
const fetchCategories = async () => {
  try {
    isLoading.value = true;
    error.value = null;
    const response = await categoriesService.getAllCategories();

    if (response.data && Array.isArray(response.data)) {
      categories.value = response.data.map(category => ({
        id: category.id,
        name: locale.value === 'no' ? category.nameNo : category.nameEn,
        icon: category.url,
        rawData: category
      }));
    } else if (response.data && response.data.categories) {
      categories.value = response.data.categories.map(category => ({
        id: category.id,
        name: locale.value === 'no' ? category.nameNo : category.nameEn,
        icon: category.url,
        rawData: category
      }));
    } else {
      console.error('Unexpected API response format:', response.data);
      error.value = t('productPage.errorLoadingCategories');
    }
  } catch (err) {
    console.error('Error fetching categories:', err);
    error.value = t('productPage.errorLoadingCategories');
  } finally {
    isLoading.value = false;
  }
};

// Handle category selection
const selectCategory = (categoryId) => {
  isRecommendedActive.value = false;

  if (selectedCategory.value === categoryId) {
    selectedCategory.value = null;
  } else {
    selectedCategory.value = categoryId;
  }

  // Reset the listings in the store when changing categories
  // This ensures we don't append to existing listings
  listingStore.$patch({
    listings: [],
    totalElements: 0,
    totalPages: 0,
    currentPage: 0
  });

  emit('select-category', selectedCategory.value, false);
};

// Handle recommended selection
const handleRecommendedSelected = (recommendedData) => {
  isRecommendedActive.value = true;
  selectedCategory.value = -1;

  // Use the recommended data to update the store if available
  if (recommendedData && recommendedData.elements) {
    listingStore.$patch({
      listings: recommendedData.elements,
      totalElements: recommendedData.totalElements,
      totalPages: recommendedData.totalPages,
      currentPage: recommendedData.currentPage,
      pageSize: recommendedData.pageSize,
      hasNext: recommendedData.hasNext,
      hasPrevious: recommendedData.hasPrevious
    });
  }

  emit('select-category', -1, true);
};

// Update category names when language changes
watch(() => locale.value, () => {
  if (categories.value.length > 0) {
    categories.value = categories.value.map(cat => ({
      ...cat,
      name: locale.value === 'no' ? cat.rawData.nameNo : cat.rawData.nameEn
    }));
  }
});

onMounted(fetchCategories);
</script>

<template>
  <div class="categories-container">
    <h2 class="categories-title">{{ t('productPage.categories') }}</h2>

    <!-- Loading state -->
    <div v-if="isLoading" class="categories-loading">
      <div class="spinner"></div>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="categories-error">
      {{ error }}
    </div>

    <!-- Categories display -->
    <div v-else class="categories-row">
      <!-- Recommended Button -->
      <RecommendedButton
          :class="{ 'active': isRecommendedActive }"
          @recommended-selected="handleRecommendedSelected"
          class="category-button"
      />

      <!-- Category buttons -->
      <div
          v-for="category in categories"
          :key="category.id"
          class="category-button"
          :class="{ 'active': selectedCategory === category.id && !isRecommendedActive }"
          @click="selectCategory(category.id)"
      >
        <div class="category-content">
          <div class="category-icon">
            <img v-if="category.icon" :src="category.icon" :alt="category.name" />
            <div v-else class="placeholder-icon"></div>
          </div>
          <div class="category-name">{{ category.name }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.categories-container {
  width: 100%;
  max-width: 1000px;
  margin-bottom: 2rem;
}

.categories-title {
  font-size: 1.5rem;
  margin-bottom: 15px;
  color: #333;
  font-weight: 600;
}

.categories-row {
  display: flex;
  gap: 15px;
  margin-top: 1rem;
  flex-wrap: wrap;
  justify-content: center;
}

.category-button {
  width: 100px;
  height: 100px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
  display: flex;
  justify-content: center;
  align-items: center;
}

.category-button:hover {
  background-color: #f5f5f5;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.category-button.active {
  background-color: #f0f7ff;
  border-color: #3b82f6;
  box-shadow: 0 4px 8px rgba(59, 130, 246, 0.2);
}

.category-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.category-icon {
  margin-bottom: 8px;
  height: 32px;
  width: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-icon img {
  max-width: 30px;
  max-height: 30px;
  object-fit: contain;
}

.placeholder-icon {
  width: 30px;
  height: 30px;
  background-color: #e5e7eb;
  border-radius: 4px;
}

.category-name {
  font-size: 1.2rem;
  text-align: center;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  overflow: hidden;
  word-break: break-word;
  hyphens: auto;
}

.categories-loading {
  display: flex;
  justify-content: center;
  padding: 2rem 0;
}

.spinner {
  width: 2.5rem;
  height: 2.5rem;
  border: 0.25rem solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.categories-error {
  color: #ef4444;
  text-align: center;
  padding: 1rem;
  background: #fee2e2;
  border-radius: 0.5rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .categories-row {
    justify-content: center;
  }

  .category-button {
    width: 100px;
    height: 100px;
    padding: 8px 5px;
  }

  .category-name {
    font-size: 0.8rem;
  }
}

@media (max-width: 480px) {
  .category-button {
    width: 90px;
    height: 90px;
  }
}
</style>