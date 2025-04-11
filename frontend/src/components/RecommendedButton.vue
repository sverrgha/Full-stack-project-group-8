<!-- RecommendedButton.vue -->
<script setup>
import { ref } from 'vue';
import { useListingStore } from '../stores/listing';
import { useAuthStore } from '../stores/auth';
import { useI18n } from 'vue-i18n';
import starIcon from '../assets/star.svg';

const { t } = useI18n();
const emit = defineEmits(['recommended-selected']);

const listingStore = useListingStore();
const authStore = useAuthStore();
const isLoading = ref(false);

const handleRecommendedClick = async () => {
  try {
    isLoading.value = true;

    // Get current user ID
    const userId = authStore.user?.id;

    // Create pageable object according to API requirements
    const pageable = {
      page: 0,
      size: 20,
    };

    // Call the API through the store
    const response = await listingStore.fetchRecommendedListings(userId, pageable);

    // Emit event to parent component with the response data
    emit('recommended-selected', response);
  } catch (error) {
    console.error('Error fetching recommended listings:', error);
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="recommended-wrapper" @click="handleRecommendedClick">
    <div class="category-content">
      <div class="category-icon">
        <img :src="starIcon" alt="Star" class="star-icon" />
      </div>
      <div class="category-name">{{ t('productPage.recommended') || 'Recommended' }}</div>
    </div>
  </div>
</template>

<style scoped>
.recommended-wrapper {
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

.recommended-wrapper:hover {
  background-color: #f5f5f5;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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

.star-icon {
  width: 30px;
  height: 30px;
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

@media (max-width: 768px) {
  .recommended-wrapper {
    width: 100px;
    height: 100px;
    padding: 8px 5px;
  }

  .category-name {
    font-size: 0.8rem;
  }
}

@media (max-width: 480px) {
  .recommended-wrapper {
    width: 90px;
    height: 90px;
  }
}
</style>