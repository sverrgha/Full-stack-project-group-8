<script setup>
import {ref, onMounted, computed} from 'vue';
import {useRoute} from 'vue-router';
import {useI18n} from 'vue-i18n';
import ImageGallery from '../components/Gallery.vue';
import BaseInputField from '../components/form/BaseInputField.vue';
import TextAreaField from "../components/form/TextAreaField.vue";
import {useListingStore} from "../stores/listing.js";
import {useAuthStore} from "../stores/auth.js";

const route = useRoute();
const {t} = useI18n();
const listingStore = useListingStore();
const authStore = useAuthStore();
const product = ref({});
const loading = ref(true);
const error = ref(null);
const isEditing = ref(false);
const showStatusDropdown = ref(false);
const statusOptions = [
  {value: 'available', label: t('itemDetailPage.available')},
  {value: 'sold', label: t('itemDetailPage.sold')},
  {value: 'reserved', label: t('itemDetailPage.reserved')},
  {value: 'archived', label: t('itemDetailPage.archived')}
];


const isOwner = computed(() => {
  if (!product.value || !authStore.getUser) return false;
  return product.value.userId === authStore.getUser.id;
});

// Fetch product details based on the route parameter
onMounted(async () => {
  const productId = route.params.id;
  try {
    await listingStore.fetchListingById(productId);
    // Map backend data to component format
    if (listingStore.currentListing) {
      product.value = {
        id: listingStore.currentListing.id,
        name: listingStore.currentListing.title,
        description: listingStore.currentListing.description,
        price: listingStore.currentListing.price,
        location: listingStore.currentListing.city,
        userId: listingStore.currentListing.userId,
        seller: listingStore.currentListing.userId,
        category: listingStore.currentListing.categoryId,
        condition: listingStore.currentListing.condition,
        images: listingStore.currentListing.images || [],
        postedDate: new Date(listingStore.currentListing.createdAt).toLocaleDateString()
      };
    }
  } catch (err) {
    error.value = "Failed to load product details: " + (err.message || err);
  } finally {
    loading.value = false;
  }
});


const saveChanges = async () => {
  try {
    // Implement the update logic using your API
    // Example: await listingService.updateListing(product.value.id, mappedData);
    isEditing.value = false;
  } catch (err) {
    error.value = "Failed to save changes";
  }
};

// Delete item function
const deleteItem = async () => {
  if (confirm(t('itemDetailPage.confirmDelete') || 'Are you sure you want to delete this item?')) {
    try {
      // Implement the delete logic using your API
      // Example: await listingService.deleteListing(product.value.id);
      // Redirect to listings page or home
      // router.push('/listings');
    } catch (err) {
      error.value = "Failed to delete item";
    }
  }
};

const toggleStatusDropdown = () => {
  showStatusDropdown.value = !showStatusDropdown.value;
};

const updateStatus = async (status) => {
  await listingStore.updateListingStatus(product.value.id, status);
  product.value.status = status;
  showStatusDropdown.value = false;
}
const updateImages = (newImages) => {
  product.value.images = newImages;
  //implement api here
};
</script>

<template>
  <div class="product-detail-container card">
    <div v-if="loading" class="loading">
      {{ t('common.loading') || 'Loading...' }}
    </div>

    <div v-else-if="error" class="error">
      {{ error }}
    </div>

    <div v-else>
      <div class="product-details">
        <div class="product-images card">
          <!-- Image Gallery -->
          <ImageGallery
              v-model:images="product.images"
              :is-editing="isEditing"
              :max-images="10"
              @update:images="updateImages"
          />
        </div>

        <div class="product-info">
          <!-- Owner Actions Bar for editing -->
          <div v-if="isOwner" class="owner-actions">
            <div class="button-container">
              <button v-if="!isEditing" @click="toggleEditMode" class="action-button edit-button">
                {{ t('itemDetailPage.edit') }}
              </button>
              <button v-if="!isEditing" @click="deleteItem" class="action-button delete-button">
                {{ t('itemDetailPage.delete') }}
              </button>
              <div v-if="isOwner" class="dropdown-menu">
                <button @click="toggleStatusDropdown" class="action-button">
                  {{ product.status }}
                </button>
                <div v-if="showStatusDropdown" class="dropdown-menu">
                  <button v-for="option in statusOptions"
                          :key="option.value"
                          @click="updateStatus(option.value)"
                          class="status-option">
                    {{ option.label }}
                  </button>
                </div>
              </div>
              <button v-if="isEditing" @click="saveChanges" class="action-button save-button">
                {{ t('itemDetailPage.save') }}
              </button>
              <button v-if="isEditing" @click="toggleEditMode" class="action-button cancel-button">
                {{ t('itemDetailPage.cancel') }}
              </button>
            </div>
          </div>

          <!-- Item info when not owner -->
          <template v-if="!isEditing">
            <h1 class="product-title">{{ product.name }}</h1>
            <div class="price-box">{{ product.price }} kr</div>
            <div class="location-box">
              <span class="label">{{ t('itemDetailPage.location') }}:</span>
              <span>{{ product.location }}</span>
            </div>
            <div class="posted-date">
              <span class="label">{{ t('itemDetailPage.dateAdded') }}:</span>
              <span>{{ product.postedDate }}</span>
            </div>
            <div class="seller-info">
              <h3>{{ t('itemDetailPage.seller') }}</h3>
              <div>{{ product.seller }}</div>
              <button v-if="!isOwner" class="contact-button">
                {{ t('itemDetailPage.contactSeller') }}
              </button>
            </div>
          </template>

          <!-- Item info when editing -->
          <template v-else>
            <BaseInputField
                v-model="product.name"
                type="text"
                class="title-input"
            />

            <!-- Price input -->
            <div class="price-edit">
              <BaseInputField
                  v-model="product.price"
                  type="number"
                  class="price-input"
              />
              <span> kr</span>
            </div>

            <!-- Location input -->
            <div class="edit-row">
              <span class="label">{{ t('itemDetailPage.location') }}:</span>
              <BaseInputField
                  v-model="product.location"
                  type="text"
                  class="edit-field"
              />
            </div>

            <!-- Category input -->
            <div class="edit-row">
              <span class="label">{{ t('itemDetailPage.category') }}:</span>
              <select v-model="product.category" class="edit-field select-input">
                <option v-for="category in availableCategories" :key="category" :value="category">
                  {{ category }}
                </option>
              </select>
            </div>

            <!-- Condition input -->
            <div class="edit-row">
              <span class="label">{{ t('itemDetailPage.condition') }}:</span>
              <select v-model="product.condition" class="edit-field select-input">
                <option v-for="condition in availableConditions" :key="condition" :value="condition">
                  {{ condition }}
                </option>
              </select>
            </div>
          </template>
        </div>
      </div>

      <!-- Description  -->
      <div class="product-description card">
        <h2>{{ t('itemDetailPage.description') }}</h2>
        <TextAreaField
            v-if="isEditing"
            id="product-description"
            label=""
            v-model="product.description"
            rows="6"
        />
        <p v-else>{{ product.description }}</p>
      </div>

      <!-- Product details -->
      <div class="product-details-table card">
        <h2>{{ t('itemDetailPage.itemDetails') }}</h2>
        <div class="detail-row">
          <span class="detail-label">{{ t('itemDetailPage.category') }}</span>
          <span class="detail-value">{{ product.category }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('itemDetailPage.condition') }}</span>
          <span class="detail-value">{{ product.condition }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('itemDetailPage.id') }}</span>
          <span class="detail-value">{{ product.id }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-detail-container {
  max-width: 1200px;
  margin: 30px auto;
  padding: 20px;
  background: white;
  position: relative;
}

.product-details {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  margin-bottom: 30px;
}

.product-images {
  flex: 1;
  min-width: 300px;
}

.product-info {
  flex: 1;
  min-width: 300px;
}

.product-title {
  margin-top: 0;
  font-size: 28px;
}

.price-box {
  font-size: 24px;
  font-weight: bold;
  margin: 15px 0;
}

.location-box, .posted-date {
  margin: 10px 0;
}

.label {
  font-weight: 500;
  margin-right: 5px;
}

.seller-info {
  margin-top: 30px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
}

.contact-button {
  background: #4CAF50;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  margin-top: 10px;
  cursor: pointer;
}

.card {
  width: 100%;
  box-sizing: border-box;
  overflow-wrap: break-word;
}

.product-description {
  margin: 30px 0;
}

.product-details-table {
  margin: 30px 0;
}

.detail-row {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.detail-label {
  flex: 1;
  font-weight: 500;
}

.detail-value {
  flex: 2;
}

.loading, .error {
  padding: 20px;
  text-align: center;
}

.error {
  color: red;
}

.owner-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 15px;
}

.button-container {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.action-button {
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  font-size: 18px;
  cursor: pointer;
  white-space: nowrap;
  min-width: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-button {
  background-color: #2196F3;
  color: white;
}

.delete-button {
  background-color: #f44336;
  color: white;
}

.save-button {
  background-color: #4CAF50;
  color: white;
}

.cancel-button {
  background-color: #757575;
  color: white;
}

.edit-field {
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: inherit;
}

.title-input {
  font-size: 24px;
  font-weight: bold;
}

.price-edit {
  display: flex;
  align-items: center;
  margin: 15px 0;
}

.price-input {
  font-size: 20px;
  font-weight: bold;
  width: 100px;
  margin-right: 5px;
}

.edit-row :deep(.form-group) {
  border: none;
}

.edit-row {
  margin: 10px 0;
  display: flex;
  align-items: center;
}

.select-input {
  height: 38px;
  background-color: white;
  cursor: pointer;
  appearance: auto;
}

.select-input option {
  padding: 8px;
}

.edit-row .label {
  width: 100px;
}

.edit-row .edit-field {
  flex: 1;
  margin-bottom: 0;
}

@media (max-width: 768px) {
  .button-container {
    justify-content: flex-start;
  }

  .action-button {
    flex: 1;
    min-width: unset;
    padding: 8px;
  }
}

@media (max-width: 480px) {
  .button-container {
    flex-direction: column;
    width: 100%;
  }

  .action-button {
    width: 100%;
  }
}
</style>