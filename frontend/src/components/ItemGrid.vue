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
</script>

<template>
  <div class="grid">
    <ItemCard
        v-for="item in items"
        :key="item.id"
        :id="item.id"
        :title="item.title"
        :location="item.location"
        :price="item.price"
        :imageUrl="item.imageUrl"
    />

    <!-- Show a message if there are no items -->
    <div v-if="items.length === 0" class="empty-state">
      No items to display
    </div>
  </div>
</template>

<style scoped>
.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 2rem;
  color: #666;
  font-style: italic;
}
</style>