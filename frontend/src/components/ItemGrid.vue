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
  console.log('First item details:', JSON.stringify(props.items[0]));
}
</script>

<template>
  <div class="grid">
    <ItemCard
        v-for="item in items"
        :key="item.id"
        :id="item.id"
        :title="item.title"
        :location="item.city"
        :price="item.price"
        :imageUrl="item.pathToImage"
    />

    <!-- Show a message if there are no items -->
    <div v-if="items.length === 0" class="empty-state">
      No items to display
    </div>
  </div>
</template>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 2rem;
  color: #666;
  font-style: italic;
}
</style>