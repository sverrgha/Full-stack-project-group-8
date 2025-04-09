<script setup>
import { ref, defineProps, defineEmits } from 'vue';
import { ref as storageRef, uploadBytes, getDownloadURL } from 'firebase/storage';
import { storage } from '../FirebaseConfig.js'; // Import your configured storage
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps({
  images: {
    type: Array,
    default: () => []
  },
  maxImages: {
    type: Number,
    default: 5
  }
});

const emit = defineEmits(['update:images', 'imagesReady']);

// Local state
const fileList = ref([]); // Stores the actual file objects
const previewImages = ref([...props.images]); // Stores preview URLs
const uploadProgress = ref(0);
const isUploading = ref(false);
const errorMessage = ref('');

// Add new image to preview
const addImage = (event) => {
  const files = event.target.files;
  if (!files.length) return;

  if (previewImages.value.length + files.length > props.maxImages) {
    errorMessage.value = t('newListing.maxImagesError') || `Maximum ${props.maxImages} images allowed`;
    setTimeout(() => {
      errorMessage.value = '';
    }, 3000);
    return;
  }

  Array.from(files).forEach(file => {
    // Store the actual file for later upload
    fileList.value.push(file);

    // Generate preview
    const reader = new FileReader();
    reader.onload = (e) => {
      previewImages.value.push(e.target.result);
      // Emit the preview images to parent
      emit('update:images', previewImages.value);
    };
    reader.readAsDataURL(file);
  });

  event.target.value = '';
};

// Remove image
const removeImage = (index) => {
  previewImages.value.splice(index, 1);
  fileList.value.splice(index, 1);
  emit('update:images', previewImages.value);
};

// Upload all images to Firebase when form submits
const uploadImagesToFirebase = async () => {
  if (fileList.value.length === 0) {
    // No new images to upload
    emit('imagesReady', []);
    return [];
  }

  isUploading.value = true;
  errorMessage.value = '';

  const uploadedImageUrls = [];

  try {
    for (let i = 0; i < fileList.value.length; i++) {
      const file = fileList.value[i];
      const fileExtension = file.name.split('.').pop();
      const fileName = `listings/${Date.now()}-${i}.${fileExtension}`;
      const imageRef = storageRef(storage, fileName);

      // Upload the file
      await uploadBytes(imageRef, file);

      // Get the download URL
      const downloadURL = await getDownloadURL(imageRef);
      uploadedImageUrls.push(downloadURL);

      // Update progress
      uploadProgress.value = Math.round(((i + 1) / fileList.value.length) * 100);
    }

    // Reset local state after successful upload
    fileList.value = [];
    emit('imagesReady', uploadedImageUrls);
    return uploadedImageUrls;
  } catch (error) {
    console.error("Error uploading images:", error);
    errorMessage.value = t('newListing.imageUploadError') || 'Failed to upload images';
    throw error;
  } finally {
    isUploading.value = false;
    uploadProgress.value = 0;
  }
};

// Expose the upload function
defineExpose({ uploadImagesToFirebase });
</script>

<template>
  <div class="image-upload">
    <!-- Error message display -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <!-- Upload progress -->
    <div v-if="isUploading" class="upload-progress">
      <div class="progress-bar">
        <div class="progress-fill" :style="`width: ${uploadProgress}%`"></div>
      </div>
      <span>{{ uploadProgress }}%</span>
    </div>

    <!-- Preview images -->
    <div class="image-preview-grid">
      <div
          v-for="(image, index) in previewImages"
          :key="index"
          class="image-preview-container"
      >
        <img :src="image" class="image-preview" alt="Preview" />
        <button
            @click="removeImage(index)"
            class="remove-image-btn"
            type="button"
        >×</button>
      </div>

      <!-- Add image button -->
      <div v-if="previewImages.length < maxImages" class="add-image-container">
        <label for="image-upload" class="add-image-btn">
          <span>+</span>
          <span class="add-text">{{ t('newListing.addImage') || 'Add Image' }}</span>
        </label>
        <input
            id="image-upload"
            type="file"
            accept="image/*"
            multiple
            @change="addImage"
            style="display: none"
        >
      </div>
    </div>

    <p class="image-count">
      {{ previewImages.length }}/{{ maxImages }} {{ t('itemDetailPage.maxPictures') || 'images' }}
    </p>
  </div>
</template>

<style scoped>
.image-upload {
  margin-bottom: 20px;
  width: 100%;
}

.error-message {
  color: #e53935;
  margin-bottom: 10px;
  padding: 8px;
  background-color: rgba(229, 57, 53, 0.1);
  border-radius: 4px;
}

.upload-progress {
  margin: 10px 0;
}

.progress-bar {
  height: 8px;
  background-color: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #2196F3;
  transition: width 0.3s ease;
}

.image-preview-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.image-preview-container {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #eee;
}

.image-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 24px;
  height: 24px;
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
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px dashed #ccc;
  border-radius: 4px;
  cursor: pointer;
}

.add-image-btn {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666;
}

.add-image-btn span:first-child {
  font-size: 24px;
}

.add-text {
  font-size: 12px;
  margin-top: 5px;
}

.image-count {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

@media (max-width: 480px) {
  .image-preview-container,
  .add-image-container {
    width: 80px;
    height: 80px;
  }
}
</style>