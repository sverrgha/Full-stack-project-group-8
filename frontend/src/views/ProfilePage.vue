<script setup>
import { ref, computed } from 'vue'
import ProfileInfo from '../components/profile/ProfileInfo.vue'
import ProfilePostsNav from '../components/profile/ProfilePostsNav.vue'
import ItemGrid from '../components/ItemGrid.vue'

// Posts as default tab state
const activeTab = ref('posts')

// Mock data for items
const posts = [
  {
    id: 1,
    title: 'Chair',
    location: 'Oslo',
    price: 1200,
    imageUrl: 'https://placehold.co/300x300?text=Chair'
  },
  {
    id: 2,
    title: 'Desk',
    location: 'Bergen',
    price: 2500,
    imageUrl: 'https://placehold.co/300x300?text=Desk'
  },
  {
    id: 3,
    title: 'Skis',
    location: 'Trondheim',
    price: 3000,
    imageUrl: 'https://placehold.co/300x300?text=Skis'
  },
  {
    id: 4,
    title: 'Snowboard',
    location: 'Oslo',
    price: 2700,
    imageUrl: 'https://placehold.co/300x300?text=Snowboard'
  }
]

const favorites = [
  {
    id: 5,
    title: 'Bicycle',
    location: 'Oslo',
    price: 4500,
    imageUrl: 'https://placehold.co/300x300?text=Bicycle'
  },
  {
    id: 6,
    title: 'Tickets',
    location: 'Stavanger',
    price: 800,
    imageUrl: 'https://placehold.co/300x300?text=Tickets'
  },
  {
    id: 7,
    title: 'Pants',
    location: 'Bergen',
    price: 600,
    imageUrl: 'https://placehold.co/300x300?text=Pants'
  },
  {
    id: 8,
    title: 'Shoes',
    location: 'Oslo',
    price: 1200,
    imageUrl: 'https://placehold.co/300x300?text=Shoes'
  },
  {
    id: 9,
    title: 'Suit',
    location: 'Trondheim',
    price: 3500,
    imageUrl: 'https://placehold.co/300x300?text=Suit'
  },
  {
    id: 10,
    title: 'Phone',
    location: 'Bergen',
    price: 6000,
    imageUrl: 'https://placehold.co/300x300?text=Phone'
  },
  {
    id: 11,
    title: 'Laptop',
    location: 'Oslo',
    price: 12000,
    imageUrl: 'https://placehold.co/300x300?text=Laptop'
  }
]
const purchased = []

const itemsToShow = computed(() => {
  if (activeTab.value === 'posts') return posts
  if (activeTab.value === 'favorites') return favorites
  return purchased
})
</script>

<template>
  <div class="profile-page">
    <ProfileInfo />
    <div class="profile-posts card">
      <!-- Reference to access the component instance -->
      <ProfilePostsNav v-model="activeTab" />
      <!-- Pass the items from the computed property -->
      <ItemGrid :items="itemsToShow" class="grid-layout" />
    </div>
  </div>
</template>

<style>
/* Global shared styles */
:root {
  --card-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
  --card-radius: 12px;
}

.profile-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
}

.card {
  position: relative;
  border-radius: var(--card-radius);
  box-shadow: var(--card-shadow);
  padding: 2rem;
  margin-bottom: 2rem;
  width: 100%;
}

.profile-posts {
  margin-top: 2rem;
  padding-bottom: 2rem;
}

.grid-layout {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 3rem;
  transition: all 0.3s ease;
  min-height: 700px;
  justify-content: space-between;
  grid-auto-rows: minmax(min-content, max-content);
}

@media (max-width: 768px) {
  .grid-layout {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}
</style>