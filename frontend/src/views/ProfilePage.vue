<script setup>
import {ref, computed, watch, onMounted} from 'vue'
import ProfileInfo from '../components/profile/ProfileInfo.vue'
import ProfilePostsNav from '../components/profile/ProfilePostsNav.vue'
import ItemGrid from '../components/ItemGrid.vue'
import {useListingStore} from "../stores/listing.js";
import {useAuthStore} from "../stores/auth.js";

const authStore = useAuthStore();
const listingStore = useListingStore();
// Posts as default tab state
const activeTab = ref('posts');
const userId = authStore.getUser.id;

const posts = ref([])
const favorites = ref([])
const currentPage = ref(1)
const pageSize = ref(20)

const itemsToShow = computed(() => {
  if (activeTab.value === 'posts') return posts
  return favorites
})

watch(activeTab, async () => {
  await fetchPosts()
})

const fetchPosts = async () => {
  if (activeTab.value === 'posts') {
    await listingStore.fetchPersonalListings(userId, currentPage, pageSize)
    posts.value = listingStore.listings
  }
  else {
    await listingStore.fetchFavoriteListings(userId, currentPage, pageSize)
    favorites.value = listingStore.listings
  }
}

onMounted(async () => {
  await fetchPosts()
})
</script>

<template>
  <div class="profile-page">
    <ProfileInfo />
    <div class="profile-posts card">
      <!-- Reference to access the component instance -->
      <ProfilePostsNav v-model="activeTab" />
      <!-- Pass the items from the computed property -->
      <ItemGrid :items="itemsToShow.value" class="grid-layout" />
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