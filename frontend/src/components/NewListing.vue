<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { debounce } from 'lodash' // Assuming you have lodash installed

const router = useRouter()
const { t } = useI18n()
const isSubmitting = ref(false)
const formRef = ref(null)
const dragActive = ref(false)

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

// Character counts and limits
const charLimits = {
  title: 20,
  briefDescription: 100,
  description: 2000
}

// Compute remaining characters
const charsUsed = computed(() => ({
  title: form.title.length,
  briefDescription: form.briefDescription.length,
  description:form.description.length
}))

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
const fileInput = ref(null)

// Set up debounced validation
const debouncedValidate = debounce((field) => {
  validateField(field)
}, 300)

// Handle image upload
const handleImageUpload = (event) => {
  const files = Array.from(event.target.files || event.dataTransfer.files || [])
  if (!files.length) return

  if (files.length + form.images.length > 5) {
    errors.images = 'newListing.maxImagesError'
    return
  }

  form.images = [...form.images, ...files]

  // Generate preview URLs
  files.forEach(file => {
    if (!file.type.match('image.*')) return

    const reader = new FileReader()
    reader.onload = (e) => {
      imagePreviewUrls.value.push({
        url: e.target.result,
        name: file.name,
        size: formatFileSize(file.size)
      })
    }
    reader.readAsDataURL(file)
  })

  errors.images = ''

  // Reset the input to allow selecting the same file again if needed
  if (fileInput.value) fileInput.value.value = ''
}

// Format file size for display
const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B'
  else if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB'
  else return (bytes / 1048576).toFixed(1) + ' MB'
}

// Drag and drop handlers
const handleDragEnter = (e) => {
  e.preventDefault()
  e.stopPropagation()
  dragActive.value = true
}

const handleDragLeave = (e) => {
  e.preventDefault()
  e.stopPropagation()
  dragActive.value = false
}

const handleDragOver = (e) => {
  e.preventDefault()
  e.stopPropagation()
}

const handleDrop = (e) => {
  e.preventDefault()
  e.stopPropagation()
  dragActive.value = false
  handleImageUpload(e)
}

// Remove image
const removeImage = (index) => {
  imagePreviewUrls.value.splice(index, 1)
  form.images.splice(index, 1)
  validateField('images')
}

// Field-level validation
const validateField = (field) => {
  let isValid = true

  if (field === 'title' || field === 'all') {
    if (!form.title.trim()) {
      errors.title = 'newListing.titleRequired'
      isValid = false
    } else {
      errors.title = ''
    }
  }

  if (field === 'description' || field === 'all') {
    if (!form.description.trim() || form.description.length < 10) {
      errors.description = 'newListing.descriptionRequired'
      isValid = false
    } else {
      errors.description = ''
    }
  }

  if (field === 'price' || field === 'all') {
    if (!form.price || isNaN(form.price) || parseFloat(form.price) <= 0) {
      errors.price = 'newListing.invalidPrice'
      isValid = false
    } else {
      errors.price = ''
    }
  }

  if (field === 'category' || field === 'all') {
    if (!form.category) {
      errors.category = 'newListing.categoryRequired'
      isValid = false
    } else {
      errors.category = ''
    }
  }

  if (field === 'images' || field === 'all') {
    if (form.images.length === 0) {
      errors.images = 'newListing.imageRequired'
      isValid = false
    } else {
      errors.images = ''
    }
  }

  return isValid
}

// Full form validation
const validateForm = () => {
  return validateField('all')
}

const clearErrors = () => {
  for (const key in errors) {
    errors[key] = ''
  }
}

// Handle condition selection with keyboard
const handleConditionKeyDown = (event, condition) => {
  if (event.key === 'Enter' || event.key === ' ') {
    event.preventDefault()
    form.condition = condition
  }
}

// Submit form
const createListing = async () => {
  if (!validateForm()) {
    // Scroll to the first error
    const firstErrorField = formRef.value.querySelector('.invalid')
    if (firstErrorField) {
      firstErrorField.scrollIntoView({ behavior: 'smooth', block: 'center' })
      firstErrorField.focus()
    }
    return
  }

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

// Focus the title field on mount
onMounted(() => {
  const titleInput = document.getElementById('title')
  if (titleInput) titleInput.focus()
})
</script>

<template>
  <div class="new-listing-container">
    <div class="new-listing-form card">
      <h1>{{ t('newListing.createNewListing') }}</h1>

      <form @submit.prevent="createListing" ref="formRef">
        <!-- Title -->
        <div class="form-group">
          <label for="title">{{ t('newListing.title') }} <span class="required">*</span></label>
          <div class="input-wrapper">
            <input
                type="text"
                id="title"
                v-model="form.title"
                :class="{ invalid: errors.title }"
                :aria-invalid="errors.title ? 'true' : 'false'"
                :aria-describedby="errors.title ? 'title-error' : null"
                maxlength="20"
                @input="debouncedValidate('title')"
            />
            <span class="char-count">
              {{ charsUsed.title }}/20
            </span>
          </div>
          <p v-if="errors.title" id="title-error" class="error-message">{{ t(errors.title) }}</p>
        </div>

        <!--brief-description -->
        <div class="form-group">
          <label for="briefDescription">{{ t('newListing.brief-description') }}</label>
          <div class="input-wrapper">
            <input
                type="text"
                id="briefDescription"
                v-model="form.briefDescription"
                maxLength="100"
                @input="debouncedValidate('briefDescription')"
            />
            <span class="char-count">
              {{ charsUsed.briefDescription }}/{{ charLimits.briefDescription}}
            </span>
          </div>
        </div>

        <!-- Description -->
        <div class="form-group">
          <label for="description">{{ t('newListing.description') }} <span class="required">*</span></label>
          <div class="input-wrapper">
            <textarea
                id="description"
                v-model="form.description"
                :class="{ invalid: errors.description }"
                :aria-invalid="errors.description ? 'true' : 'false'"
                :aria-describedby="errors.description ? 'description-error' : null"
                rows="5"
                maxlength="2000"
                @input="debouncedValidate('description')"
            ></textarea>
            <span class="char-count">
              {{ charsUsed.description }}/{{ charLimits.description}}
            </span>
          </div>
          <p v-if="errors.description" id="description-error" class="error-message">{{ t(errors.description) }}</p>
        </div>

        <!-- Price -->
        <div class="form-group">
          <label for="price">{{ t('newListing.price') }} <span class="required">*</span></label>
          <div class="price-input">
            <input
                type="number"
                id="price"
                v-model="form.price"
                :class="{ invalid: errors.price }"
                :aria-invalid="errors.price ? 'true' : 'false'"
                :aria-describedby="errors.price ? 'price-error' : null"
                min="0"
                step="0.01"
                @input="debouncedValidate('price')"
            />
            <span class="currency-symbol">kr</span>
          </div>
          <p v-if="errors.price" id="price-error" class="error-message">{{ t(errors.price) }}</p>
        </div>

        <!-- Category -->
        <div class="form-group">
          <label for="category">{{ t('newListing.category') }} <span class="required">*</span></label>
          <select
              id="category"
              v-model="form.category"
              :class="{ invalid: errors.category }"
              :aria-invalid="errors.category ? 'true' : 'false'"
              :aria-describedby="errors.category ? 'category-error' : null"
              @change="validateField('category')"
          >
            <option value="" disabled>{{ t('newListing.selectCategory') }}</option>
            <option v-for="category in categories" :key="category" :value="category">
              {{ t(`categories.${category}`) }}
            </option>
          </select>
          <p v-if="errors.category" id="category-error" class="error-message">{{ t(errors.category) }}</p>
        </div>

        <!-- Condition -->
        <div class="form-group">
          <label id="condition-label">{{ t('newListing.condition') }}</label>
          <div
              class="condition-options"
              role="radiogroup"
              aria-labelledby="condition-label"
          >
            <div
                v-for="condition in conditions"
                :key="condition"
                class="condition-option"
                :class="{ selected: form.condition === condition }"
                @click="form.condition = condition"
                @keydown="handleConditionKeyDown($event, condition)"
                role="radio"
                :aria-checked="form.condition === condition"
                tabindex="0"
            >
              {{ t(`conditions.${condition}`) }}
            </div>
          </div>
        </div>

        <!-- Images -->
        <div class="form-group">
          <label id="images-label">
            {{ t('newListing.images') }} <span class="required">*</span>
            <span class="max-images-note">({{ t('newListing.maxImages') }})</span>
          </label>
          <div
              class="image-upload-area"
              :class="{
              invalid: errors.images,
              'drag-active': dragActive
            }"
              @click="fileInput.click()"
              @dragenter="handleDragEnter"
              @dragleave="handleDragLeave"
              @dragover="handleDragOver"
              @drop="handleDrop"
              :aria-invalid="errors.images ? 'true' : 'false'"
              :aria-describedby="errors.images ? 'images-error' : null"
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
              <img src="../assets/upload.svg" alt="" class="upload-icon" aria-hidden="true" />
              <p>{{ t('newListing.dragOrClick') }}</p>
            </div>
          </div>
          <p v-if="errors.images" id="images-error" class="error-message">{{ t(errors.images) }}</p>

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
                  :aria-label="`${t('newListing.removeImage')} ${image.name}`"
              >
                ×
              </button>
            </div>
          </div>
        </div>

        <!-- Required fields note -->
        <div class="required-fields-note">
          <span class="required">*</span> {{ t('common.requiredField') }}
        </div>

        <!-- Buttons -->
        <div class="form-actions">
          <button type="button" class="btn secondary" @click="cancel">
            {{ t('common.cancel') }}
          </button>
          <button
              type="submit"
              class="btn primary"
              :disabled="isSubmitting"
          >
            <span class="spinner" v-if="isSubmitting"></span>
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

.input-wrapper {
  position: relative;
}

input, textarea, select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  transition: all 0.2s;
  font-size: 1rem;
  background-color: var(--bg-color, white);
  color: var(--text-color, #333);
}

input:focus, textarea:focus, select:focus {
  border-color: #4f46e5;
  outline: none;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.2);
}

input.invalid, textarea.invalid, select.invalid, .image-upload-area.invalid {
  border-color: #e53e3e;
}

input.invalid:focus, textarea.invalid:focus, select.invalid:focus {
  box-shadow: 0 0 0 3px rgba(229, 62, 62, 0.2);
}

.char-count {
  position: absolute;
  right: 10px;
  bottom: 10px;
  font-size: 0.8rem;
  color: #666;
  pointer-events: none;
}

.error-message {
  color: #e53e3e;
  font-size: 0.85rem;
  margin-top: 0.25rem;
  animation: fadeIn 0.2s;
}

.helper-text {
  color: #666;
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
  gap: 10px;
}

.condition-option {
  padding: 10px 16px;
  border: 1px solid #ccc;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}

.condition-option:hover {
  border-color: #4f46e5;
  background-color: rgba(79, 70, 229, 0.05);
}

.condition-option:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.2);
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

.upload-formats {
  font-size: 0.85rem;
  color: #666;
  margin-top: 0.5rem;
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

.required-fields-note {
  font-size: 0.85rem;
  color: #666;
  margin-top: 1rem;
  margin-bottom: 1rem;
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
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn.primary {
  background-color: #4f46e5;
  color: white;
  min-width: 120px;
}

.btn.primary:hover {
  background-color: #4338ca;
  transform: translateY(-2px);
}

.btn.primary:active {
  transform: translateY(0);
}

.btn.primary:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.4);
}

.btn.primary:disabled {
  background-color: #a5a5a5;
  cursor: not-allowed;
  transform: none;
}

.btn.secondary {
  background-color: #f3f4f6;
  color: #374151;
}

.btn.secondary:hover {
  background-color: #e5e7eb;
  transform: translateY(-2px);
}

.btn.secondary:active {
  transform: translateY(0);
}

.btn.secondary:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(55, 65, 81, 0.2);
}

.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid #ffffff;
  border-top-color: transparent;
  border-radius: 50%;
  margin-right: 8px;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@media (max-width: 676px) {
  .new-listing-form {
    padding: 1.5rem;
    margin: 1rem 0;
  }

  .condition-options {
    flex-direction: column;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .btn {
    width: 100%;
  }

  .image-previews {
    justify-content: center;
  }
}
</style>