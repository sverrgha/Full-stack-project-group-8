<script setup>
import ItemCard from './ItemCard.vue'

const props = defineProps({
  items: {
    type: Array,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  },
  currentPage: {
    type: Number,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['page-change'])

const handlePageChange = (page) => {
  emit('page-change', page)
}
</script>

<template>
  <div class="grid-container">
    <!-- Grid of items -->
    <div class="items-wrapper">
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
    </div>

    <div v-if="items.length === 0" class="empty-state">
      No items to display
    </div>

  </div>

  <!-- Footer section with pagination -->
  <div v-if="totalPages > 1" class="grid-footer">
    <div class="pagination">
      <button
          :disabled="currentPage === 1"
          @click="handlePageChange(currentPage - 1)"
          class="pagination-button"
      >
        Previous
      </button>

      <div class="page-numbers">
        <button
            v-for="page in totalPages"
            :key="page"
            :class="['page-number', { active: page === currentPage }]"
            @click="handlePageChange(page)"
        >
          {{ page }}
        </button>
      </div>

      <button
          :disabled="currentPage === totalPages"
          @click="handlePageChange(currentPage + 1)"
          class="pagination-button"
      >
        Next
      </button>
    </div>
  </div>
</template>

<style scoped>
.grid-container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  width: 100%;
}

.items-wrapper {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  width: 100%;
}

.item-card-wrapper {
  width: 100%;
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: #666;
  font-style: italic;
}

.grid-footer {
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #e5e7eb;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
}

.page-numbers {
  display: flex;
  gap: 0.5rem;
}

.pagination-button,
.page-number {
  padding: 0.5rem 1rem;
  border: 1px solid #e5e7eb;
  background-color: white;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-button:hover:not(:disabled),
.page-number:hover {
  background-color: #f9fafb;
  border-color: #d1d5db;
}

.page-number.active {
  background-color: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

@media (max-width: 640px) {
  .pagination {
    flex-direction: column;
    gap: 0.75rem;
  }

  .page-numbers {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>