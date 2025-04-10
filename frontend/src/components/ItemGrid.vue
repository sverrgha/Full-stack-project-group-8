<!-- ItemGrid.vue - Grid display of items -->
<script setup>
import ItemCard from './ItemCard.vue'

// Props to receive the active tab and item data
const props = defineProps({
  items: {
    type: Array,
    required: 'true'
  }
})

if (props.items.length > 0) {
}
</script>

<template>
  <div class="grid-container">
    <div class="grid">
      <!-- Loop through items and create ItemCard components -->
      <ItemCard
          v-for="item in items"
          :key="item.id"
          :id="item.id"
          :title="item.title"
          :location="item.city"
          :price="item.price"
          :imageUrl="item.pathToImage"
          class="item-card-wrapper"
      />

      <!-- Show a message if there are no items -->
      <div v-if="items.length === 0" class="empty-state">
        No items to display
      </div>
    </div>
  </div>
</template>

<style scoped>
.grid-container {
  width: 100%;
}

.grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  width: 100%;
  justify-content: center;
}

.item-card-wrapper {
  max-width: 200px;
  width: 200px;
  margin-right: 20px;
  margin-bottom: 20px;
  flex-shrink: 0;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 2rem;
  color: #666;
  font-style: italic;
}

@media (max-width: 1200px) {
  .grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 800px) {
  .grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>