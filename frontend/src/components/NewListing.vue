<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { t } = useI18n()
const isSubmitting = ref(false)

// Form data structure
const form = reactive({
  title: '',
  briefDescription: '',
  description: '',
  price: '',
  category: '',
  condition: 'new',
  images: []
})

// Form validation errors
const errors = reactive({
  title: '',
  briefDescription: '',
  description: '',
  price: '',
  category: '',
  images: ''
})

// Available categories
const categories = [
  'electronics',
  'clothing',
  'furniture',
  'books',
  'sports',
  'other'
]

// Available conditions
const conditions = [
  'new',
  'likeNew',
  'good',
  'fair',
  'poor'
]

// Image preview
const imagePreviewUrls = ref([])

// Handle image upload
const handleImageUpload = (event) => {
  const files = Array.from(event.target.files)
  if (files.length + form.images.length > 5) {
    errors.images = 'newListing.maxImagesError'
    return
  }

  form.images = [...form.images, ...files]

  // Generate preview URLs
  files.forEach(file => {
    const reader = new FileReader()
    reader.onload = (e) => {
      imagePreviewUrls.value.push(e.target.result)
    }
    reader.readAsDataURL(file)
  })

  errors.images = ''
}

// Remove image
const removeImage = (index) => {
  imagePreviewUrls.value.splice(index, 1)
  form.images.splice(index, 1)
}

// Form validation
const validateForm = () => {
  let isValid = true
  clearErrors()

  if (!form.title.trim()) {
    errors.title = 'newListing.titleRequired'
    isValid = false
  }

  if (!form.description.trim() || form.description.length < 10) {
    errors.description = 'newListing.descriptionRequired'
    isValid = false
  }

  if (!form.price || isNaN(form.price) || parseFloat(form.price) <= 0) {
    errors.price = 'newListing.invalidPrice'
    isValid = false
  }

  if (!form.category) {
    errors.category = 'newListing.categoryRequired'
    isValid = false
  }

  if (form.images.length === 0) {
    errors.images = 'newListing.imageRequired'
    isValid = false
  }

  return isValid
}

const clearErrors = () => {
  for (const key in errors) {
    errors[key] = ''
  }
}

// Submit form
const createListing = async () => {
  if (!validateForm()) return

  isSubmitting.value = true

  try {
    // Here you would implement your API call to submit the form
    // const formData = new FormData()
    // for (const key in form) {
    //   if (key === 'images') {
    //     form.images.forEach(image => {
    //       formData.append('images', image)
    //     })
    //   } else {
    //     formData.append(key, form[key])
    //   }
    // }
    // await api.post('/listings', formData)

    console.log('Creating listing:', form)

    // Mock API delay
    await new Promise(resolve => setTimeout(resolve, 1000))

    // Redirect to product page or listing success page
    router.push('/products')
  } catch (error) {
    console.error('Error creating listing:', error)
  } finally {
    isSubmitting.value = false
  }
}

const cancel = () => {
  router.go(-1)
}
</script>

<template>
  <div class="new-listing-container">
    <div class="new-listing-form card">
      <h1>{{ t('newListing.createNewListing') }}</h1>

      <form @submit.prevent="createListing">
        <!-- Title -->
        <div class="form-group">
          <label for="title">{{ t('newListing.title') }}</label>
          <input
              type="text"
              id="title"
              v-model="form.title"
              :class="{ invalid: errors.title }"
              maxlength="100"
          />
          <p v-if="errors.title" class="error-message">{{ t(errors.title) }}</p>
        </div>

        <!--brief-description -->
        <div class="form-group">
          <label for="brief description">{{ t('newListing.brief-description') }}</label>
          <input
          type="text"
          id="briefDescription"
          v-model="form.briefDescription"
          maxLength="100"
          />
        </div>

        <!-- Description -->
        <div class="form-group">
          <label for="description">{{ t('newListing.description') }}</label>
          <textarea
              id="description"
              v-model="form.description"
              :class="{ invalid: errors.description }"
              rows="5"
              maxlength="2000"
          ></textarea>
          <p v-if="errors.description" class="error-message">{{ t(errors.description) }}</p>
        </div>

        <!-- Price -->
        <div class="form-group">
          <label for="price">{{ t('newListing.price') }}</label>
          <div class="price-input">
            <input
                type="number"
                id="price"
                v-model="form.price"
                :class="{ invalid: errors.price }"
                min="0"
            />
            <span class="currency-symbol">kr</span>
          </div>
          <p v-if="errors.price" class="error-message">{{ t(errors.price) }}</p>
        </div>

        <!-- Category -->
        <div class="form-group">
          <label for="category">{{ t('newListing.category') }}</label>
          <select
              id="category"
              v-model="form.category"
              :class="{ invalid: errors.category }"
          >
            <option value="" disabled>{{ t('newListing.selectCategory') }}</option>
            <option v-for="category in categories" :key="category" :value="category">
              {{ t(`categories.${category}`) }}
            </option>
          </select>
          <p v-if="errors.category" class="error-message">{{ t(errors.category) }}</p>
        </div>

        <!-- Condition -->
        <div class="form-group">
          <label>{{ t('newListing.condition') }}</label>
          <div class="condition-options">
            <div
                v-for="condition in conditions"
                :key="condition"
                class="condition-option"
                :class="{ selected: form.condition === condition }"
                @click="form.condition = condition"
            >
              {{ t(`conditions.${condition}`) }}
            </div>
          </div>
        </div>

        <!-- Images -->
        <div class="form-group">
          <label>{{ t('newListing.images') }} ({{ t('newListing.maxImages') }})</label>
          <div
              class="image-upload-area"
              :class="{ invalid: errors.images }"
              @click="$refs.fileInput.click()"
          >
            <input
                type="file"
                ref="fileInput"
                @change="handleImageUpload"
                accept="image/*"
                multiple
                class="file-input"
            />
            <div v-if="imagePreviewUrls.length === 0" class="upload-placeholder">
              <img src="../assets/upload.svg" alt="Upload" class="upload-icon" />
              <p>{{ t('newListing.dragOrClick') }}</p>
            </div>
          </div>
          <p v-if="errors.images" class="error-message">{{ t(errors.images) }}</p>

          <!-- Image previews -->
          <div v-if="imagePreviewUrls.length > 0" class="image-previews">
            <div
                v-for="(url, index) in imagePreviewUrls"
                :key="index"
                class="image-preview"
            >
              <img :src="url" alt="Preview" />
              <button type="button" class="remove-image" @click="removeImage(index)">×</button>
            </div>
          </div>
        </div>

        <!-- Buttons -->
        <div class="form-actions">
          <button type="button" class="btn secondary" @click="cancel">
            {{ t('common.cancel') }}
          </button>
          <button type="submit" class="btn primary" :disabled="isSubmitting">
            {{ isSubmitting ? t('common.submitting') : t('newListing.createListing') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.new-listing-container {
  max-width: 800px;
  margin: 3rem auto;
  padding: 0 1rem;
}

.new-listing-form {
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
  background-color: var(--bg-color, white);
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: var(--text-color, #333);
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--text-color, #444);
  font-weight: 600;
}

input, textarea, select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  transition: border-color 0.2s;
  font-size: 1rem;
  background-color: var(--bg-color, white);
  color: var(--text-color, #333);
}

input:focus, textarea:focus, select:focus {
  border-color: #4f46e5;
  outline: none;
}

input.invalid, textarea.invalid, select.invalid, .image-upload-area.invalid {
  border-color: red;
}

.error-message {
  color: red;
  font-size: 0.85rem;
  margin-top: 0.25rem;
}

.price-input {
  position: relative;
  display: flex;
  align-items: center;
  max-width: 200px;
}

input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.currency-symbol {
  position: absolute;
  right: 10px;
  font-weight: bold;
}

.condition-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.condition-option {
  padding: 8px 16px;
  border: 1px solid #ccc;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.condition-option.selected {
  background-color: #4f46e5;
  color: white;
  border-color: #4f46e5;
}

.image-upload-area {
  border: 2px dashed #ccc;
  border-radius: 8px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.2s;
}

.image-upload-area:hover {
  border-color: #4f46e5;
}

.file-input {
  display: none;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.upload-icon {
  width: 48px;
  height: 48px;
  opacity: 0.6;
}

.image-previews {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 1rem;
}

.image-preview {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 16px;
  cursor: pointer;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn.primary {
  background-color: #4f46e5;
  color: white;
}

.btn.primary:hover {
  background-color: #4338ca;
}

.btn.primary:disabled {
  background-color: #a5a5a5;
  cursor: not-allowed;
}

.btn.secondary {
  background-color: #f3f4f6;
  color: #374151;
}

.btn.secondary:hover {
  background-color: #e5e7eb;
}

@media (max-width: 676px) {
  .new-listing-form {
    padding: 1.5rem;
  }

  .condition-options {
    flex-direction: column;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}
</style>