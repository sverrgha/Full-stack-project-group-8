<script setup>
import { ref } from 'vue';
import ItemGrid from '../components/ItemGrid.vue'
import Search from '../components/SearchBar.vue'
import CategoryButtons from "../components/CategoryButtons.vue";
import Sort from '../components/Sort.vue'
import FilterButton from '../components/FilterButton.vue';
import FilterSidebar from '../components/FilterSidebar.vue';

// Mock product items
const products = [
  {
    id: 101,
    title: 'Leather Sofa',
    location: 'Oslo',
    price: 8500,
    imageUrl: 'https://placehold.co/300x300?text=Leather+Sofa'
  },
  {
    id: 102,
    title: 'Coffee Table',
    location: 'Bergen',
    price: 1800,
    imageUrl: 'https://placehold.co/300x300?text=Coffee+Table'
  },
  {
    id: 103,
    title: 'Dining Set',
    location: 'Trondheim',
    price: 5200,
    imageUrl: 'https://placehold.co/300x300?text=Dining+Set'
  },
  {
    id: 104,
    title: 'Bookshelf',
    location: 'Stavanger',
    price: 2100,
    imageUrl: 'https://placehold.co/300x300?text=Bookshelf'
  },
  {
    id: 105,
    title: 'Floor Lamp',
    location: 'Oslo',
    price: 950,
    imageUrl: 'https://placehold.co/300x300?text=Floor+Lamp'
  }
]

// State to track if sidebar is open
const isSidebarOpen = ref(false);

// Toggle sidebar visibility
const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value;

  // Prevent scrolling on body when sidebar is open
  if (isSidebarOpen.value) {
    document.body.style.overflow = 'hidden';
  } else {
    document.body.style.overflow = 'auto';
  }
};

// Close sidebar
const closeSidebar = () => {
  isSidebarOpen.value = false;
  document.body.style.overflow = 'auto';
};
</script>

<template>
  <div class="products-page">
    <!-- Filter Sidebar -->
    <FilterSidebar
        :isOpen="isSidebarOpen"
        @close="closeSidebar"
    />

    <div class="top-controls">
      <div class="searchbar">
        <Search />
      </div>

      <div class="filter-sort-controls">
        <FilterButton
            :isOpen="isSidebarOpen"
            @toggle="toggleSidebar"
        />
        <Sort />
      </div>
    </div>

    <div class="category-buttons card">
      <CategoryButtons />
    </div>

    <div class="products-posts card">
      <ItemGrid :items="products" Class="grid-layout" />
    </div>
  </div>
</template>

<style scoped>
.products-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
  position: relative;
}

.top-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 2rem;
  margin-bottom: 2rem;
}

.searchbar {
  flex: 1;
  max-width: 500px;
}

.filter-sort-controls {
  display: flex;
  gap: 12px;
  align-items: center;
}

.category-buttons {
  margin-top: 2rem;
  margin-bottom: 2rem;
}

.products-posts {
  margin-top: 2rem;
  padding-bottom: 2rem;
}

.grid-layout {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
  gap: 3rem;
  transition: all 0.3s ease;
  min-height: 700px;
  justify-content: space-between;
  grid-auto-rows: minmax(min-content, max-content);
  width: 100%;
}

@media (max-width: 768px) {
  .top-controls {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .searchbar {
    max-width: 100%;
    width: 100%;
  }

  .filter-sort-controls {
    width: 100%;
    justify-content: space-between;
  }

  .grid-layout {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}
</style>