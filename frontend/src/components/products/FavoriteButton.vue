<script setup>

import {ref} from "vue";
import heartEmpty from "../../assets/heartEmpty.svg";
import heartFilled from "../../assets/heartFilled.svg";

const props = defineProps({
  isFavorite: {
    type: Boolean,
    default: false
  },
  listingId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['toggleFavorite'])

const isLoading = ref(false);

const toggleFavorite = async () => {
  if (isLoading.value) return;
  isLoading.value = true;
  try {
    emit('toggleFavorite', {
      listingId: props.listingId,
      isFavorite: !props.isFavorite
    });
  } catch (error) {
    console.error("Error toggling favorite:", error);
  } finally {
    isLoading.value = false;
  }
}

</script>

<template>
  <button
      class="favorite-button"
      :class="{ 'is-favorite': isFavorite, 'is-loading': isLoading }"
      @click="toggleFavorite"
      aria-label="Toggle favorite"
  >
    <img
        :src="isFavorite ? heartFilled : heartEmpty"
        class="heart-icon"
        :class="{ 'heart-icon-true': isFavorite }"
        alt="Heart"
    />
    <span v-if="isLoading" class="loading-spinner"></span>
  </button>
</template>

<style scoped>
.favorite-button {
  background-color: gra;
  border-radius: 15%;
  cursor: pointer;
  padding: 8px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  outline: none;
  border: none;
  transition: transform 0.15s ease, box-shadow 0.3s ease;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.favorite-button:hover {
  transform: scale(1.1);
  background-color: #6b7280;
}


.heart-icon {
  width: 24px;
  height: 24px;
  stroke: #000;
  stroke-width: 2;
  fill: transparent;
  transition: all 0.3s ease;
}

.is-loading {
  pointer-events: none;
  opacity: 0.7;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(0, 0, 0, 0.1);
  border-top-color: #4f46e5;
  border-radius: 50%;
  position: absolute;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>