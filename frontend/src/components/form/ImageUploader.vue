<script setup>
import { ref, defineProps, defineEmits } from 'vue';
import {useI18n} from "vue-i18n";

import uploadIcon from '/src/assets/upload.svg';

const props = defineProps({
  maxImages: {
    type: Number,
    default: 5
  },
  images: {
    type: Array,
    default: () => []
  },
  error: {
    type: String,
    default: ''
  },
  label: {
    type: String,
    required: true
  },
  required: {
    type: Boolean,
    default: false
  }
});

const { t } = useI18n()

const emit = defineEmits(['update:images']);

const fileInput = ref(null);
const dragActive = ref(false);
const imagePreviewUrls = ref([]);

// Format file size for display
const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B';
  else if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
  else return (bytes / 1048576).toFixed(1) + ' MB';
};

// Handle image upload
const handleImageUpload = (event) => {
  const files = Array.from(event.target.files || event.dataTransfer.files || []);
  if (!files.length) return;

  if (files.length + props.images.length > props.maxImages) {
    emit('update:images', { error: true, message: 'maxImagesError' });
    return;
  }

  const newImages = [...props.images, ...files];
  emit('update:images', { images: newImages, error: false });

  // Generate preview URLs
  files.forEach(file => {
    if (!file.type.match('image.*')) return;

    const reader = new FileReader();
    reader.onload = (e) => {
      imagePreviewUrls.value.push({
        url: e.target.result,
        name: file.name,
        size: formatFileSize(file.size)
      });
    };
    reader.readAsDataURL(file);
  });

  // Reset the input to allow selecting the same file again if needed
  if (fileInput.value) fileInput.value.value = '';
};

// Drag and drop handlers
const handleDragEnter = (e) => {
  e.preventDefault();
  e.stopPropagation();
  dragActive.value = true;
};

const handleDragLeave = (e) => {
  e.preventDefault();
  e.stopPropagation();
  dragActive.value = false;
};

const handleDragOver = (e) => {
  e.preventDefault();
  e.stopPropagation();
};

const handleDrop = (e) => {
  e.preventDefault();
  e.stopPropagation();
  dragActive.value = false;
  handleImageUpload(e);
};

// Remove image
const removeImage = (index) => {
  imagePreviewUrls.value.splice(index, 1);
  const newImages = [...props.images];
  newImages.splice(index, 1);
  emit('update:images', { images: newImages, error: false });
};
</script>

<template>
  <div class="form-group">
    <label id="images-label">
      {{ label }} <span v-if="required" class="required">*</span>
      <span class="max-images-note">({{ t('newListing.maxImages') }})</span>
    </label>
    <div
        class="image-upload-area"
        :class="{
        'invalid': error,
        'drag-active': dragActive
      }"
        @click="fileInput.click()"
        @dragenter="handleDragEnter"
        @dragleave="handleDragLeave"
        @dragover="handleDragOver"
        @drop="handleDrop"
        :aria-invalid="error ? 'true' : 'false'"
        :aria-describedby="error ? 'images-error' : null"
    >
      <input
          type="file"
          ref="fileInput"
          @change="handleImageUpload"
          accept="image/*"
          multiple
          class="file-input"
          aria-labelledby="images-label"
      />
      <div v-if="imagePreviewUrls.length === 0" class="upload-placeholder">
        <img :src="uploadIcon" alt="" class="upload-icon" aria-hidden="true" />
        <p>{{ t('newListing.dragOrClick') }}</p>
      </div>
    </div>
    <p v-if="error" id="images-error" class="error-message">{{ error }}</p>

    <!-- Image previews -->
    <div v-if="imagePreviewUrls.length > 0" class="image-previews">
      <div
          v-for="(image, index) in imagePreviewUrls"
          :key="index"
          class="image-preview"
      >
        <img :src="image.url" :alt="image.name" />
        <div class="image-info">
          <span class="image-name">{{ image.name }}</span>
          <span class="image-size">{{ image.size }}</span>
        </div>
        <button
            type="button"
            class="remove-image"
            @click="removeImage(index)"
            :aria-label="`Remove image ${image.name}`"
        >
          ×
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--text-color, #444);
  font-weight: 600;
}

.required {
  color: #e53e3e;
  margin-left: 2px;
}

.max-images-note {
  font-weight: normal;
  color: #666;
  font-size: 0.9em;
  margin-left: 5px;
}

.image-upload-area {
  border: 2px dashed #ccc;
  border-radius: 8px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.image-upload-area:hover {
  border-color: #4f46e5;
  background-color: rgba(79, 70, 229, 0.05);
}

.image-upload-area.drag-active {
  border-color: #4f46e5;
  background-color: rgba(79, 70, 229, 0.1);
}

.image-upload-area.invalid {
  border-color: #e53e3e;
}

.file-input {
  display: none;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.upload-icon {
  width: 48px;
  height: 48px;
  opacity: 0.6;
  margin-bottom: 0.5rem;
}

.image-previews {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 1rem;
}

.image-preview {
  position: relative;
  width: 120px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.image-preview:hover {
  transform: translateY(-3px);
}

.image-preview img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.image-info {
  padding: 8px;
  background-color: #f8f8f8;
  font-size: 0.75rem;
}

.image-name {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 2px;
}

.image-size {
  color: #666;
}

.remove-image {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.remove-image:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

.error-message {
  color: #e53e3e;
  font-size: 0.85rem;
  margin-top: 0.25rem;
  animation: fadeIn 0.2s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>