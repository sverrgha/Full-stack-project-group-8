<!-- frontend/src/components/ItemCard.vue -->
<script setup>
import { defineProps } from 'vue'

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
    required: true
  },
  imageUrl: {
    type: String,
    default: ''
  }
})
</script>

<template>
  <div class="item-card">
    <div class="image-container">
      <img :src="imageUrl" :alt="title" />
    </div>
    <div class="content">
      <p class="location">{{ location }}</p>
      <h3 class="title">{{ title }}</h3>
      <p class="price">{{ price}}kr</p>
    </div>
  </div>
</template>

<style scoped>
.item-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
  background: white;
  max-width: 300px;
}

.item-card:hover {
  transform: translateY(-5px);
}

.image-container {
  height: 300px;
  overflow: hidden;
}

.image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.content {
  padding: 1rem;
}

.title {
  font-size: 1.2rem;
  margin: 0 0 0.5rem;
}

.location {
  color: #666;
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.price {
  font-weight: bold;
  font-size: 1.3rem;
}
</style>