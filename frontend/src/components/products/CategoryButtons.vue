<script setup>
import {computed, ref} from 'vue';

import { useI18n } from 'vue-i18n';

// Import icons (SVGs)
import VehicleIcon from '../../assets/vehicle.svg';
import ClothingIcon from '../../assets/clothing.svg';
import FurnitureIcon from '../../assets/furniture.svg';
import PropertyIcon from '../../assets/property.svg';
import ActivityIcon from '../../assets/activity.svg';
import ElectronicsIcon from '../../assets/electronics.svg';
import BeautyIcon from '../../assets/beauty.svg';

const { t } = useI18n();

const emit = defineEmits(['select-category'])

// Define shopping categories
const categories = computed(() => [
  { id: 1, name: t('productPage.vehicle'), icon: VehicleIcon  },
  { id: 2, name: t('productPage.clothing'), icon: ClothingIcon },
  { id: 3, name: t('productPage.interior'), icon: FurnitureIcon },
  { id: 4, name: t('productPage.property'), icon: PropertyIcon },
  { id: 5, name: t('productPage.activity'), icon: ActivityIcon },
  { id: 6, name: t('productPage.electronics'), icon: ElectronicsIcon },
  { id: 7, name: t('productPage.beauty'), icon: BeautyIcon },
]);

// ref variable to hold the selected category, default is null
const selectedCategory = ref(null);

// Function to select a category, filtering items based on the selected category
const selectCategory = (category) => {
  emit('select-category', category)
};
</script>

<template>
  <div class="categories-container">
    <h2 class="categories-title">{{t('productPage.categories')}}</h2>

    <!-- Category buttons -->
    <div class="categories-row">
      <div
          v-for="category in categories"
          :key="category.id"
          class="category-button"
          :class="{ 'active': selectedCategory === category.id }"
          @click="selectCategory(category.id)"
      >
        <div class="category-icon">
          <img :src="category.icon" :alt="category.name" />
        </div>
        <div class="category-name">{{ category.name }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.categories-container {
  width: 100%;
  max-width: 1000px;
}

.categories-title {
  font-size: 1.5rem;
  margin-bottom: 15px;
  color: #333;
}

.categories-row {
  display: flex;
  gap: 15px;
}

.category-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
  font-size: 20px;
}

.category-button:hover {
  background-color: #f5f5f5;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.category-button {
  background-color: #f0f7ff;
  border-color: #3b82f6;
  box-shadow: 0 4px 8px rgba(59, 130, 246, 0.2);
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
}

.category-name {
  font-size: 1.2rem;
  text-align: center;
}

/* Mobile Styles */
@media (max-width: 768px) {
  .categories-row {
    flex-wrap: wrap;
    justify-content: center;
  }

  .category-button {
    flex: 0 0 calc(33.333% - 15px);
    min-width: 80px;
    padding: 8px 5px;
  }

  .category-name {
    font-size: 0.8rem;
  }
}

@media (max-width: 480px) {
  .category-button {
    flex: 0 0 calc(50% - 15px);
  }
}
</style>