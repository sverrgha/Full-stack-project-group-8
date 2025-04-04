<!-- ProfilePosts.vue - Lower part of the ProfilePage,
including categorise, and items-->

<script setup>
import { ref, computed } from 'vue'
import ItemCard from './ItemCard.vue'

// Active tab. Default is 'posts'
const activeTab = ref('posts')

// Mock data for items, replacing the simple string arrays
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

// Show items based on active tab
const itemsToShow = computed(() => {
  if (activeTab.value === 'posts') return posts
  if (activeTab.value === 'favorites') return favorites
  return purchased
})
</script>

<template>
  <div class="profile-posts card">
    <!-- Tab navigation - keep this exactly as it was -->
    <div class="tab-nav">
      <button :class="{ active: activeTab === 'posts' }" @click="activeTab = 'posts'">
        <img src="../assets/grid.svg" class="icon" /> Posts
      </button>
      <button :class="{ active: activeTab === 'favorites' }" @click="activeTab = 'favorites'">
        <img src="../assets/heart1.svg" class="icon" /> Favorites
      </button>
      <button :class="{ active: activeTab === 'purchased' }" @click="activeTab = 'purchased'">
        <img src="../assets/purchased.svg" class="icon" /> Purchased
      </button>
    </div>

    <!-- Items grid with ItemCard components -->
    <div class="grid">
      <ItemCard
          v-for="item in itemsToShow"
          :key="item.id"
          :id="item.id"
          :title="item.title"
          :location="item.location"
          :price="item.price"
          :imageUrl="item.imageUrl"
      />
      <!-- Show a message if there are no items -->
      <div v-if="itemsToShow.length === 0" class="empty-state">
        No items to display
      </div>
    </div>
  </div>
</template>

<style scoped>
.tab-nav {
  padding-bottom: 2rem;
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 2.5rem;
}

.tab-nav button {
  background: none;
  border: none;
  padding: 0.75rem 0;
  font-weight: bold;
  font-size: 1.2rem;
  color: #666;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.tab-nav button:hover {
  background-color: #d1d5db;
}

.tab-nav button.active {
  color: #4f46e5;
  border-bottom: 2px solid #4f46e5;
  font-weight: 600;
}

.tab-nav .icon {
  width: 25px;
  height: 25px;
  vertical-align: middle;
  padding-right: 1rem;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 3rem;
  transition: all 0.3s ease;
  min-height: 700px;
  justify-content: space-between;
  grid-auto-rows: minmax(min-content, max-content);
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 2rem;
  color: #666;
  font-style: italic;
}

@media (max-width: 768px) {
  .tab-nav button {
    width: auto;
    font-size: 1rem;
  }

  .grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}
</style>