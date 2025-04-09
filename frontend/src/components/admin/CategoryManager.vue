<script setup>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import BaseInputField from '../form/BaseInputField.vue'; // Adjust path as needed

const { t } = useI18n();

// Props
const props = defineProps({
  initialCategories: {
    type: Array,
    default: () => []
  }
});

// Category management
const categories = ref([...props.initialCategories]);
const newCategory = ref({ name: '', nameNo: '' });
const editingIndex = ref(-1);
const editingCategory = ref({ name: '', nameNo: '' });

// Emits
const emit = defineEmits(['update:categories']);

// Add Category
const addCategory = () => {
  if (newCategory.value.name && newCategory.value.nameNo) {
    categories.value.push({...newCategory.value});
    newCategory.value = { name: '', nameNo: '' };
    emit('update:categories', categories.value);
  }
};

// Edit Category
const startEdit = (index) => {
  editingIndex.value = index;
  editingCategory.value = {...categories.value[index]};
};

// Save Edit
const saveEdit = () => {
  if (editingIndex.value >= 0 && editingCategory.value.name && editingCategory.value.nameNo) {
    categories.value[editingIndex.value] = {...editingCategory.value};
    emit('update:categories', categories.value);
    cancelEdit();
  }
};

// Cancel Edit
const cancelEdit = () => {
  editingIndex.value = -1;
  editingCategory.value = { name: '', nameNo: '' };
};

// Delete Category
const deleteCategory = (index) => {
  if (confirm('Are you sure you want to delete this category?')) {
    categories.value.splice(index, 1);
    emit('update:categories', categories.value);
  }
};
</script>

<template>
  <div class="category-management card">
    <h2>{{ t('admin.showCategoriesButton') }}</h2>

    <!-- Add new category -->
    <div class="add-form">
      <h3>{{ t('admin.addNewCategoryButton') }}</h3>
      <div class="form-group">
        <BaseInputField
            v-model="newCategory.name"
            type="text"
            :label="t('admin.englishName')"
            :placeholder="t('admin.nameInEnglish')"
        />
      </div>
      <div class="form-group">
        <BaseInputField
            v-model="newCategory.nameNo"
            type="text"
            :label="t('admin.norwegianName')"
            :placeholder="t('admin.nameInNorwegian')"
        />
      </div>
      <button @click="addCategory" :disabled="!newCategory.name || !newCategory.nameNo">
        {{ t('admin.addCategoryButton') }}
      </button>
    </div>

    <!-- Category list -->
    <div class="category-list">
      <h3>{{ t('admin.currentCategories') }}</h3>
      <table>
        <thead>
        <tr>
          <th>{{ t('admin.englishName') }}</th>
          <th>{{ t('admin.norwegianName') }}</th>
          <th>{{ t('admin.actions') }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(category, index) in categories" :key="index">
          <template v-if="editingIndex === index">
            <!-- Edit mode -->
            <td>
              <BaseInputField
                  v-model="editingCategory.name"
                  type="text"
              />
            </td>
            <td>
              <BaseInputField
                  v-model="editingCategory.nameNo"
                  type="text"
              />
            </td>
            <td>
              <button class="save-btn" @click="saveEdit">{{ t('itemDetailPage.save') }}</button>
              <button class="cancel-btn" @click="cancelEdit">{{ t('itemDetailPage.cancel') }}</button>
            </td>
          </template>
          <template v-else>
            <!-- View mode -->
            <td>{{ category.name }}</td>
            <td>{{ category.nameNo }}</td>
            <td>
              <button class="edit-btn" @click="startEdit(index)">{{ t('itemDetailPage.edit') }}</button>
              <button class="delete-btn" @click="deleteCategory(index)">{{ t('itemDetailPage.delete') }}</button>
            </td>
          </template>
        </tr>
        </tbody>
      </table>
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
  background-color: #4CAF50;
  color: white;
}

button:disabled {
  background-color: #cccccc;
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

th:last-child {
  text-align: right;
}

td:last-child {
  text-align: right;
}

.edit-btn {
  background-color: #2196F3;
}

.save-btn {
  background-color: #4CAF50;
}

.cancel-btn {
  background-color: #9E9E9E;
}

.delete-btn {
  background-color: #F44336;
}
</style>