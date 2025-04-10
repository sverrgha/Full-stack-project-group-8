<!-- frontend/src/components/ItemCard.vue -->
<script setup>
import { computed } from 'vue';

// Define props
const props = defineProps({
  id: {
    type: Number,
    required: true
  },
  title: {
    type: String,
    required: true
  },
  location: {
    type: String,
    default: ''
  },
  price: {
    type: Number,
    default: 0
  },
  imageUrl: {
    type: String,
    default: ''
  }
});

// Handle image loading errors
const handleImageError = (event) => {
  event.target.src = '/src/assets/placeholder.svg'; // Fallback image
};

// Format price with currency
const formatPrice = (price) => {
  return `${price} kr`;
};

// Import the API base URL from environment variables
const apiBaseUrl = import.meta.env.VITE_API_URL;

// Add a computed property for the image source
const imageSrc = computed(() => {

  if (!props.imageUrl) {
    return '/src/assets/placeholder.svg';
  }

  if (props.imageUrl.startsWith('http')) {
    return props.imageUrl;
  } else if (props.imageUrl.startsWith('/')) {
    return `${apiBaseUrl}${props.imageUrl}`;
  } else {
    return `${apiBaseUrl}/${props.imageUrl}`;
  }
});
</script>

<template>
  <div class="item-card">
    <router-link :to="`/product/${id}`" class="item-link">
      <div class="image-container">
        <img
            :src="imageSrc"
            :alt="title"
            @error="handleImageError"
        />
      </div>

      <div class="item-info">
        <h3 class="item-title">{{ title }}</h3>
        <p class="item-location">{{ location }}</p>
        <p class="item-price">{{ formatPrice(price) }}</p>
      </div>
    </router-link>
  </div>
</template>

<style scoped>
.item-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.item-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.item-link {
  display: block;
  color: inherit;
  text-decoration: none;
}

.image-container {
  height: 200px;
  width: 100%;
  overflow: hidden;
  position: relative;
  border-radius: 8px 8px 0 0;
}


.image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  transition: transform 0.3s ease;
}

.item-info {
  padding: 12px;
}

.item-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-location {
  margin: 0 0 8px;
  font-size: 14px;
  color: #555;
}

.item-price {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #1e88e5;
}
</style>