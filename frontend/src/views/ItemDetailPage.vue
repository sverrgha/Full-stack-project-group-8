<script setup>
import {ref, onMounted, computed} from 'vue';
import {useRoute} from 'vue-router';
import {useI18n} from 'vue-i18n';
import ImageGallery from '../components/Gallery.vue';
import BaseInputField from '../components/form/BaseInputField.vue';
import TextAreaField from "../components/form/TextAreaField.vue";
import FavoriteButton from "../components/products/FavoriteButton.vue";
import {useListingStore} from "../stores/listing.js";
import {useAuthStore} from "../stores/auth.js";
import SelectField from "../components/form/SelectField.vue";
import router from "../router/index.js";
import { userService } from '../services/userService.js';
import {useMessageStore} from "../stores/messages.js";
import {categoriesService} from "../services/categoriesService.js";
import {validatePostalCode} from "../services/postalCodeService.js";
import * as postalCodeService from "../services/postalCodeService.js";

const route = useRoute();
const {t} = useI18n();
const listingStore = useListingStore();
const authStore = useAuthStore();
const messageStore = useMessageStore();
const product = ref({});
const loading = ref(true);
const error = ref(null);
const isEditing = ref(false);
const sellerEmail = ref(null);
const statusOptions = [
  {value: 'active', label: t('itemDetailPage.active')},
  {value: 'sold', label: t('itemDetailPage.sold')},
  {value: 'reserved', label: t('itemDetailPage.reserved')},
  {value: 'archived', label: t('itemDetailPage.archived')}
];

const categories = ref([]);

const availableConditions = ref(['new', 'like_new', 'good', 'fair', 'poor']); // Match your backend enum values
const originalProduct = ref({}); // To store the original data before editing

const fetchCategories = async () => {
  try {
    console.log('Fetching categories...');
    const response = await categoriesService.getAllCategories();
    // Check the actual response structure and adjust the mapping
    categories.value = response.data.categories.map(category => ({
      id: category.id,
      value: category.id,
      label: category.nameEn
    }));
  } catch (error) {
    console.error('Failed to fetch categories:', error);
    categories.value = [];
  }
};

const toggleEditMode = () => {
  if (isEditing.value) {
    // Discard changes and revert to original data
    product.value = JSON.parse(JSON.stringify(originalProduct.value));
  } else {
    // Store original data before entering edit mode
    originalProduct.value = JSON.parse(JSON.stringify(product.value));
  }
  isEditing.value = !isEditing.value;
};

const isOwner = computed(() => {
  if (!product.value || !authStore.getUser) return false;
  return product.value.userId === authStore.getUser.id;
});

// Fetch product details based on the route parameter
onMounted(async () => {
  const productId = route.params.id;
  try {
    // Fetch categories first
    await fetchCategories();

    // Continue with existing listing fetch
    await listingStore.fetchListingById(productId);
    if (listingStore.currentListing) {
      product.value = {
        id: listingStore.currentListing.id,
        name: listingStore.currentListing.title,
        description: listingStore.currentListing.description,
        briefDescription: listingStore.currentListing.briefDescription,
        price: listingStore.currentListing.price,
        location: {
          postalCode: listingStore.currentListing.postalCode,
          city: listingStore.currentListing.city
        },
        userId: listingStore.currentListing.userId,
        seller: listingStore.currentListing.userId,
        categoryId: listingStore.currentListing.categoryId, // Use actual categoryId
        status: listingStore.currentListing.status,
        condition: listingStore.currentListing.condition,
        images: listingStore.currentListing.images || [],
        postedDate: new Date(listingStore.currentListing.createdAt).toLocaleDateString(),
        isFavorite: listingStore.currentListing.isFavorite || false,
      };

      const sellerResponse = await userService.getUserById(listingStore.currentListing.userId);
      sellerEmail.value = sellerResponse.data.email;
    }
  } catch (err) {
    error.value = "Failed to load product details: " + (err.message || err);
  } finally {
    loading.value = false;
  }
});

const saveChanges = async () => {
  try {

    const postalCodeResponse = await postalCodeService.validatePostalCode(product.value.location.postalCode);

    if (!postalCodeResponse.valid) {
      throw new Error(t('newListing.invalidPostalCode'));
    }

    // Create the request object for the new listing
    const newListingData = {
      title: product.value.name,
      categoryId: product.value.categoryId,
      price: parseFloat(product.value.price),
      briefDescription: product.value.briefDescription || product.value.description.substring(0, 100),
      description: product.value.description,
      userId: authStore.user.id, // Add userId from auth store
      condition: product.value.condition,
      images: product.value.images,
      location: {
        postalCode: product.value.location.postalCode,
        city: postalCodeResponse.city,
        country: postalCodeResponse.country,
        longitude: postalCodeResponse.longitude,
        latitude: postalCodeResponse.latitude
      }
    };

    // Create new listing first
    const newListing = await listingStore.addListing(newListingData);

    // Delete the old listing after the new one is created
    await listingStore.deleteListing(product.value.id);
    alert(t('itemDetailPage.updateSuccess') || 'Item updated successfully');

    // Exit edit mode
    isEditing.value = false;

    // Navigate to the new listing
    await router.push(`/product/${newListing.id}`);
    window.location.reload();

  } catch (err) {
    error.value = "Failed to save changes: " + (err.message || err);
    console.error(err);
  }
};

const conditions = ref(availableConditions.value.map(condition => ({
  value: condition,
  label: t(`conditions.${condition}`) || condition
})));

// Delete item function
const isDeleting = ref(false);

const deleteItem = async () => {
  // Show confirmation dialog
  if (confirm(t('itemDetailPage.confirmDelete') || 'Are you sure you want to delete this item?')) {
    try {
      // Set loading state
      isDeleting.value = true;

      // Call API to delete the listing
      await listingStore.deleteListing(product.value.id);

      // Show success message
      alert(t('itemDetailPage.deleteSuccess') || 'Item deleted successfully');

      // Navigate to products page
      await router.push('/products');
    } catch (err) {
      // Prepare and show error message
      const errorMessage = err.response?.data || t('itemDetailPage.deleteError') || 'Failed to delete item';
      error.value = errorMessage;
      alert(errorMessage);

      console.error('Error deleting item:', err);
    } finally {
      // Reset loading state
      isDeleting.value = false;
    }
  }
};

onMounted(async () => {
  const productId = route.params.id;
  try {
    await listingStore.fetchListingById(productId);
    if (listingStore.currentListing) {
      // ... existing product mapping ...

      // Fetch seller's email
      const sellerResponse = await userService.getUserById(listingStore.currentListing.userId);
      sellerEmail.value = sellerResponse.data.email;
    }
  } catch (err) {
    error.value = "Failed to load product details: " + (err.message || err);
  } finally {
    loading.value = false;
  }
});

const updateStatus = async (status) => {
  try {
    await listingStore.updateListingStatus(product.value.id, status);
    product.value.status = status;
  } catch (error) {
    console.error('Failed to update status:', error);
  }
};

const updateImages = (newImages) => {
  product.value.images = newImages;
  //implement api here
};

const handleToggleFavorite = async (payload) => {
  try {
    if (!payload.isFavorite) {
      await listingStore.removeFavorite(authStore.getUser.id, payload.listingId);
    } else {
      await listingStore.addFavorite(authStore.getUser.id, payload.listingId);
    }
    product.value.isFavorite = payload.isFavorite;
  } catch (err) {
    error.value = "Failed to toggle favorite";
  }
};

const sendMessageToSeller = async () => {
  try {
    const currentUserEmail = authStore.user.email; // Get current user's email
    await messageStore.sendMessage(
        currentUserEmail,
        sellerEmail.value, // This should already be the receiver's email
        t('messages.productInterest')
    );
  } catch (error) {
    console.error('Failed to send message:', error);
  }
};

const contactSeller = async () => {
  if (!authStore.isAuthenticated) {
    // Redirect to login if user is not authenticated
    router.push('/login');
    return;
  }

  await sendMessageToSeller();
  // Navigate to messages with seller info as query params
  router.push({
    path: '/messages',
  });
};

const categoryName = computed(() => {
  const category = categories.value.find(c => c.id === product.value.categoryId);
  return category ? category.label : '';
});
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
                <span v-if="isDeleting">{{ t('common.deleting') }}...</span>
                <span v-else>{{ t('itemDetailPage.delete') }}</span>
              </button>
              <button v-if="isEditing" @click="saveChanges" class="action-button save-button">
                {{ t('itemDetailPage.save') }}
              </button>
              <button v-if="isEditing" @click="toggleEditMode" class="action-button cancel-button">
                {{ t('itemDetailPage.cancel') }}
              </button>
                <!-- Status Dropdown -->
                <SelectField
                    id="status"
                    v-model="product.status"
                    :options="statusOptions"
                    option-value-key="value"
                    option-label-key="label"
                    @change="(event) => updateStatus(event.target.value)"
                    :disabled="!isOwner"
                />
            </div>
          </div>

          <!-- Item info when not owner -->
          <template v-if="!isEditing">
            <h1 class="product-title">{{ product.name }}</h1>
            <div class="price-box">{{ product.price }} kr</div>
            <div class="location-box">
              <span class="label">{{ t('itemDetailPage.location') }}:</span>
              <span>{{ product.location.city }}</span>
            </div>
            <div class="posted-date">
              <span class="label">{{ t('itemDetailPage.dateAdded') }}:</span>
              <span>{{ product.postedDate }}</span>
            </div>
            <div class="seller-info">
              <h3>{{ t('itemDetailPage.seller') }}</h3>
              <div>{{ product.seller }}</div>
              <button v-if="!isOwner" class="contact-button" @click="contactSeller">
                {{ t('itemDetailPage.contactSeller') }}
              </button>
            </div>
            <div class="favorite-button">
              <FavoriteButton
                  :is-favorite="product.isFavorite"
                  :listing-id="product.id"
                  @toggle-favorite="handleToggleFavorite"
              />
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
                    v-model="product.location.postalCode"
                    type="text"
                    :placeholder="t('itemDetailPage.postalCode')"
                    class="postal-code-input"
                />

            </div>

            <!-- Category input -->
            <div class="edit-row">
              <span class="label">{{ t('itemDetailPage.category') }}:</span>
              <SelectField
                  id="category"
                  v-model="product.categoryId"
                  :options="categories"
                  option-value-key="value"
                  option-label-key="label"
                  class="edit-field"
              />
            </div>

            <!-- Condition input -->
            <div class="edit-row">
              <span class="label">{{ t('conditions.condition') }}:</span>
              <SelectField
                  id="condition"
                  v-model="product.condition"
                  :options="conditions"
                  option-value-key="value"
                  option-label-key="label"
                  class="edit-field"
              />
            </div>

            <!-- Brief Description (add this if missing) -->
            <div v-if="isEditing" class="edit-row">
              <span class="label">{{ t('itemDetailPage.briefDescription') }}:</span>
              <TextAreaField
                  id="brief-description"
                  v-model="product.briefDescription"
                  rows="2"
                  class="edit-field"
                  :maxlength="100"
              />
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
          <span class="detail-value">{{ categoryName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('itemDetailPage.condition') }}</span>
          <span class="detail-value">{{ product.condition }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('itemDetailPage.id') }}</span>
          <span class="detail-value">{{ product.id }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('itemDetailPage.status') }}</span>
          <span class="detail-value">{{ t(`itemDetailPage.${product.status}`) }}</span>
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
.edit-row {
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
}

.edit-row .label {
  min-width: 100px;
  font-weight: bold;
}

.edit-field {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.location-fields {
  display: flex;
  gap: 10px;
  flex: 1;
}

.city-input {
  flex: 2;
}

.postal-code-input {
  flex: 1;
}

.title-input {
  font-size: 1.5rem;
  width: 100%;
  margin-bottom: 1rem;
}

.price-edit {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.price-input {
  width: 120px;
  margin-right: 0.5rem;
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

.button-container :deep(select) {
  height: 44px;
  padding: 0 16px;
}
.button-container {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  max-width: 800px;
}

.button-container .action-button {
  min-width: 100px;
  height: 44px;
  padding: 0 16px;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.button-container :deep(.form-group) {
  margin-bottom: 0;
  width: 200px;
}

.button-container :deep(select) {
  height: 44px;
  padding: 0 16px;
  font-size: 16px;
}

.button-container :deep(.select-wrapper) {
  min-width: 120px;
  height: 49px;
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