<script setup>
import { ref, onMounted } from 'vue';
import CategoryManager from '../components/admin/CategoryManager.vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

// Category management
const categories = ref([]);
const showCategorySection = ref(false);

// Mock data
onMounted(() => {
  categories.value = [
    { name: 'Electronics', nameNo: 'Elektronikk' },
    { name: 'Furniture', nameNo: 'Møbler' },
    { name: 'Clothing', nameNo: 'Klær' }
  ];
});

// Toggle category management section
const toggleCategorySection = () => {
  showCategorySection.value = !showCategorySection.value;
};

// Update categories from child component
const updateCategories = (newCategories) => {
  categories.value = newCategories;
};
</script>

<template>
  <div class="admin-container card">
    <h1>{{ t('admin.dashboard') }}</h1>
    <!-- Manage categories button -->
    <button class="toggle-btn" @click="toggleCategorySection">
      {{ showCategorySection ? t('admin.hideCategoriesButton') : t('admin.showCategoriesButton') }}    </button>

    <!-- Category management section -->
    <CategoryManager
        v-if="showCategorySection"
        :initial-categories="categories"
        @update:categories="updateCategories"
    />
  </div>
</template>

<style scoped>
.admin-container{
  max-width: 800px;
  margin: 40px auto;
  padding: 20px;
}

.card {
  max-width: 800px;
  width: 100%;
  box-sizing: border-box;
  overflow-wrap: break-word;
  border-radius: 15px;
}

h1 {
  margin-bottom: 20px;
}

.toggle-btn {
  padding: 12px 24px;
  font-size: 16px;
  margin-bottom: 20px;
  color: black;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.toggle-btn:hover {
  background-color: lightgrey;
}
</style>