<script setup>
import { onMounted, ref, onBeforeUnmount } from 'vue';
import { useI18n } from 'vue-i18n';
import BaseInputField from '../form/BaseInputField.vue';
import { categoriesService } from "../../services/categoriesService.js";
import ImageUpload from "../ImageUpload.vue";

const { t } = useI18n();

// Props
const props = defineProps({
  initialCategories: {
    type: Array,
    default: () => []
  }
});

// Category management
const categories = ref(props.initialCategories);
const newCategory = ref({ nameEn: '', nameNo: '' });
const isLoading = ref(false);
const error = ref('');
const imageUploadRef = ref(null);
const categoryImage = ref([]);
const successMessage = ref('');
const successMessageTimeout = ref(null);

// Emits
const emit = defineEmits(['update:categories']);

// Function to clear success message and timeout
const clearSuccessMessage = () => {
  successMessage.value = '';
  if (successMessageTimeout.value) {
    clearTimeout(successMessageTimeout.value);
    successMessageTimeout.value = null;
  }
};

// Fetch categories
const fetchCategories = async () => {
  try {
    isLoading.value = true;
    error.value = '';
    const response = await categoriesService.getAllCategories();

    // Handle response structure with categories property
    if (response.data && response.data.categories) {
      categories.value = response.data.categories;
    } else if (Array.isArray(response.data)) {
      categories.value = response.data;
    } else {
      categories.value = [];
      console.warn('Unexpected response format:', response.data);
    }

    emit('update:categories', categories.value);
  } catch (err) {
    console.error('Error fetching categories:', err);
    error.value = t('admin.errorFetchingCategories') || 'Failed to load categories';
    categories.value = [];
  } finally {
    isLoading.value = false;
  }
};

// Handle image changes
const handleImagesUpdate = (images) => {
  categoryImage.value = images;
};

// Add Category
const addCategory = async () => {
  if (!newCategory.value.nameEn || !newCategory.value.nameNo) {
    error.value = t('admin.fillRequiredFields') || 'Please fill all required fields';
    clearSuccessMessage();
    return;
  }

  try {
    isLoading.value = true;
    error.value = ''; // Clear any previous error
    clearSuccessMessage(); // Clear any previous success message

    // Upload images first
    let uploadedUrls = [];
    if (imageUploadRef.value && categoryImage.value.length > 0) {
      uploadedUrls = await imageUploadRef.value.uploadImagesToFirebase();
      console.log("Category image uploaded with URL:", uploadedUrls[0]);
    }

    // Create the category with the image URL
    const categoryData = {
      nameEn: newCategory.value.nameEn,
      nameNo: newCategory.value.nameNo,
      url: uploadedUrls.length > 0 ? uploadedUrls[0] : ''
    };

    await categoriesService.createCategory(categoryData);
    await fetchCategories();

    // Show success message and set timeout
    successMessage.value = t('admin.categoryAddedSuccess') || 'Category added successfully!';
    successMessageTimeout.value = setTimeout(() => {
      successMessage.value = '';
    }, 3000);

    newCategory.value = { nameEn: '', nameNo: '' };
    categoryImage.value = [];
  } catch (err) {
    console.error('Error adding category:', err);
    error.value = t('admin.errorAddingCategory') || 'Failed to add category';
    clearSuccessMessage();
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchCategories);

// Clean up timeout when component unmounts
onBeforeUnmount(() => {
  clearSuccessMessage();
});
</script>

<template>
  <div class="category-management card">
    <h2>{{ t('admin.showCategoriesButton') }}</h2>

    <div v-if="successMessage" class="alert alert-success fade-out mb-3">
      {{ successMessage }}
    </div>

    <!-- Loading and error messages -->
    <div v-if="error" class="alert alert-danger mb-3">
      {{ error }}
    </div>

    <!-- Add category form -->
    <div class="add-form">
      <h3>{{ t('admin.addCategory') }}</h3>

      <form @submit.prevent="addCategory">
        <div class="form-group">
          <!-- English name field -->
          <BaseInputField
              id="category-name-en"
              v-model="newCategory.nameEn"
              type="text"
              :label="t('admin.englishName')"
              :placeholder="t('admin.nameInEnglish')"
              required
          />
        </div>
        <!-- Norwegian name field -->
        <div class="form-group">
          <BaseInputField
              id="category-name-no"
              v-model="newCategory.nameNo"
              type="text"
              :label="t('admin.norwegianName')"
              :placeholder="t('admin.nameInNorwegian')"
              required
          />
        </div>

        <!-- Image upload component -->
        <div class="form-group">
          <label class="form-label">{{ t('admin.categoryImage') }}</label>
          <ImageUpload
              folder="categories"
              v-model:images="categoryImage"
              :maxImages="1"
              ref="imageUploadRef"
              @update:images="handleImagesUpdate"
          />
        </div>

        <div class="buttons-container">
          <button type="submit" :disabled="isLoading" class="save-btn">
            <span v-if="isLoading" class="spinner"></span>
            {{ t('admin.addCategory') }}
          </button>
        </div>
      </form>
    </div>

    <!-- Category list -->
    <div v-if="!isLoading && categories.length > 0" class="category-list">
      <h3>{{ t('admin.existingCategories') }}</h3>
      <!-- Removed the alert indicating categories are read-only -->
      <table>
        <thead>
        <tr>
          <th>ID</th>
          <th>{{ t('admin.englishName') }}</th>
          <th>{{ t('admin.norwegianName') }}</th>
          <th>{{ t('admin.image') }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="category in categories" :key="category.id">
          <td>{{ category.id }}</td>
          <td>{{ category.nameEn }}</td>
          <td>{{ category.nameNo }}</td>
          <td>
            <img
                v-if="category.url"
                :src="category.url"
                alt="Category image"
                class="thumbnail"
            >
            <span v-else class="text-muted">{{ t('admin.noImage') }}</span>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div v-else-if="!isLoading" class="alert alert-info">
      {{ t('admin.noCategories') }}
    </div>

    <!-- Display loading spinner for initial load -->
    <div v-if="isLoading && categories.length === 0" class="text-center my-4">
      <div class="spinner spinner-large"></div>
    </div>
  </div>
</template>

<style scoped>
.category-management.card {
  margin-top: 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 20px;
  background-color: #f9f9f9;
}

.add-form {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 10px;
  width: 100%;
}

button {
  padding: 8px 12px;
  margin-right: 5px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.buttons-container {
  margin-top: 15px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

th, td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.thumbnail {
  max-height: 50px;
  max-width: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.save-btn {
  background-color: #4CAF50;
  color: white;
}

.alert {
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
}

.alert-danger {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.alert-success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
}

.fade-out {
  animation: fadeOut 0.5s ease-in-out 2.5s forwards;
}

@keyframes fadeOut {
  from { opacity: 1; }
  to { opacity: 0; }
}

.alert-info {
  background-color: #d1ecf1;
  color: #0c5460;
  border: 1px solid #bee5eb;
}

.spinner {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 0.2em solid currentColor;
  border-right-color: transparent;
  border-radius: 50%;
  animation: spinner-border .75s linear infinite;
  margin-right: 5px;
}

.spinner-large {
  width: 2rem;
  height: 2rem;
  border-width: 0.25em;
}

@keyframes spinner-border {
  to { transform: rotate(360deg); }
}

.text-center {
  text-align: center;
}

.my-4 {
  margin-top: 1.5rem;
  margin-bottom: 1.5rem;
}

.text-muted {
  color: #6c757d;
  font-style: italic;
}

.mb-3 {
  margin-bottom: 1rem;
}
</style>