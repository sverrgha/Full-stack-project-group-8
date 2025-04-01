<!-- ProfilePosts.vue - Lower part of the ProfilePage,
including categorise, and items-->

<script setup>
import { ref, computed } from 'vue'

// Active tab. Default is 'posts'
const activeTab = ref('posts')

// Placeholders items to categorise
const posts = ['Chair', 'Desk', 'Skis', 'Snowboard']
const favorites = ['Bicycle', 'Tickets', 'Pants', 'Shoes', 'Suit', 'Phone', 'Laptop', 'Door', 'Table', 'Cabinet']
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
    <!-- Tab navigation -->
    <div class="tab-nav">
      <button :class="{ active: activeTab === 'posts' }" @click="activeTab = 'posts'">Posts</button>
      <button :class="{ active: activeTab === 'favorites' }" @click="activeTab = 'favorites'">Favorites</button>
      <button :class="{ active: activeTab === 'purchased' }" @click="activeTab = 'purchased'">Purchased</button>
    </div>

    <!-- Items grid -->
    <div class="grid">
      <div class="thumbnail" v-for="(item, index) in itemsToShow" :key="index">
        {{ item }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.tab-nav {
  padding: 1rem;
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 2.5rem;
}

.tab-nav button {
  width: 15rem;
  padding: 0.6rem 1.2rem;
  border: none;
  background-color: #e5e7eb;
  border-radius: 8px;
  font-weight: 900;
  cursor: pointer;
  font-size: 1.2rem;
}

.tab-nav button:hover {
  background-color: #d1d5db;
}

.tab-nav button.active {
  background-color: #4f46e5;
  color: white;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
  transition: all 0.3s ease;
}

.thumbnail {
  height: 150px;
  background-color: #f3f4f6;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1rem;
  font-weight: 500;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}
</style>