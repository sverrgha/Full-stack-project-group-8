<script setup>
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

const props = defineProps({
  images: {
    type: Array,
    required: true,
    default: () => []
  },
  isEditing: {
    type: Boolean,
    default: false
  },
  maxImages: {
    type: Number,
    default: 10
  }
});

const emit = defineEmits(['update:images']);

const { t } = useI18n();
const currentImageIndex = ref(0);

// Watch for changes in the images array
watch(() => props.images.length, (newLength) => {
  if (currentImageIndex.value >= newLength) {
    currentImageIndex.value = Math.max(0, newLength - 1);
  }
});

// Image navigation - next image
const nextImage = () => {
  if (currentImageIndex.value < props.images.length - 1) {
    currentImageIndex.value++;
  } else {
    currentImageIndex.value = 0;
  }
};

// Image navigation - previous image
const prevImage = () => {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--;
  } else {
    currentImageIndex.value = props.images.length - 1;
  }
};

// Add new image
const addImage = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  if (props.images.length >= props.maxImages) {
    alert(t('newListing.maxImagesError') || `Maximum ${props.maxImages} images allowed`);
    return;
  }

  const reader = new FileReader();
  reader.onload = (e) => {
    const newImages = [...props.images, e.target.result];
    emit('update:images', newImages);
  };
  reader.readAsDataURL(file);

  event.target.value = '';
};

// Remove image
const removeImage = (index) => {
  const newImages = [...props.images];
  newImages.splice(index, 1);
  emit('update:images', newImages);
};
</script>

<template>
  <div class="image-gallery">
    <div class="main-image-container">
      <div v-if="images.length === 0" class="no-image">
        {{ t('common.noImage') || 'No image available' }}
      </div>
      <img
          v-else
          :src="images[currentImageIndex]"
          alt="Product image"
          class="main-image"
      >

      <!-- Navigation arrows -->
      <button
          v-if="images.length > 1"
          @click="prevImage"
          class="nav-button prev"
      >
        &lt;
      </button>
      <button
          v-if="images.length > 1"
          @click="nextImage"
          class="nav-button next"
      >
        &gt;
      </button>

      <!-- Image counter -->
      <div v-if="images.length > 1" class="image-counter">
        {{ currentImageIndex + 1 }}/{{ images.length }}
      </div>
    </div>

    <!-- Image management for edit mode -->
    <div v-if="isEditing" class="image-management">

      <div class="image-thumbnails">
        <div
            v-for="(image, index) in images"
            :key="index"
            class="thumbnail-container"
            :class="{ active: index === currentImageIndex }"
            @click="currentImageIndex = index"
        >
          <img :src="image" alt="Thumbnail" class="thumbnail">

          <!-- Remove image button -->
          <button class="remove-image-btn" @click.stop="removeImage(index)">×</button>
        </div>

        <!-- Add new image button -->
        <div v-if="images.length < maxImages" class="add-image-container">
          <label for="image-upload" class="add-image-btn">
            <span>+</span>
          </label>
          <input
              id="image-upload"
              type="file"
              accept="image/*"
              @change="addImage"
              style="display: none"
          >
        </div>
      </div>
      <p class="image-count">
        {{ images.length }}/{{ maxImages }} {{ t('itemDetailPage.maxPictures') }}
      </p>
    </div>
  </div>
</template>

<style scoped>
.image-gallery {
  width: 100%;
}

.main-image-container {
  width: 100%;
  height: 400px;
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center;
}

.no-image {
  color: #888;
  font-size: 16px;
}

.nav-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(0, 0, 0, 0.4);
  color: white;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.2s;
}

.nav-button:hover {
  background-color: rgba(0, 0, 0, 0.6);
}

.nav-button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.prev {
  left: 15px;
}

.next {
  right: 15px;
}

.image-counter {
  position: absolute;
  bottom: 15px;
  right: 15px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 14px;
}

.image-management {
  margin-top: 15px;
}

.image-thumbnails {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 10px;
}

.thumbnail-container {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  border: 2px solid #eee;
  cursor: pointer;
  transition: border-color 0.2s;
}

.thumbnail-container.active {
  border-color: #2196F3;
}

.thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image-btn {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 20px;
  height: 20px;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 16px;
  padding: 0;
  line-height: 1;
}

.add-image-container {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px dashed #ccc;
  border-radius: 4px;
}

.add-image-btn {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666;
  font-size: 24px;
}

.image-count {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

@media (max-width: 768px) {
  .main-image-container {
    height: 350px;
  }
}

@media (max-width: 480px) {
  .main-image-container {
    height: 280px;
  }

  .thumbnail-container,
  .add-image-container {
    width: 60px;
    height: 60px;
  }

  .nav-button {
    width: 32px;
    height: 32px;
    font-size: 16px;
  }
}
</style>