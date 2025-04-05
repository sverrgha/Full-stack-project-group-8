<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { debounce } from 'lodash'
import BaseInputField from "../components/form/BaseInputField.vue";
import NumberInputField from "../components/form/NumberInputField.vue";
import TextAreaField from "../components/form/TextAreaField.vue";
import ImageUploader from "../components/form/ImageUploader.vue";
import SelectField from "../components/form/SelectField.vue";
import RadioButtonGroup from "../components/form/RadioButtonGroup.vue";
import { validatePostalCode as postalCodeService } from "../services/postalCodeService";

const router = useRouter()
const { t } = useI18n()
const isSubmitting = ref(false)
const formRef = ref(null)
const postalCity = ref('')

// Form data structure
const form = reactive({
  title: '',
  briefDescription: '',
  description: '',
  price: '',
  category: '',
  condition: 'new',
  images: [],
  postalCode: ''
})

// Form validation errors
const errors = reactive({
  title: '',
  description: '',
  price: '',
  category: '',
  images: '',
  postalCode: ''
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

// Set up debounced validation
const debouncedValidate = debounce(async (field) => {
  await validateField(field)
}, 300)

// Field-level validation
const validateField = async (field) => {
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

  if (field === 'postalCode' || field === 'all') {
    if (!form.postalCode) {
      errors.postalCode = 'newListing.invalidPostalCode'
      postalCity.value = ''
      isValid = false
    } else {
      const result = await postalCodeService(form.postalCode)
      if (result.valid) {
        postalCity.value = result.city
        errors.postalCode = ''
      } else {
        errors.postalCode = 'newListing.invalidPostalCode'
        postalCity.value = ''
        isValid = false
      }
    }
  }

  return isValid
}

// Full form validation
const validateForm = async () => {
  return await validateField('all')
}

// Submit form
const createListing = async () => {
  if (!await validateForm()) {
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
    //api call to post listing goes here

    console.log('Creating listing:', form)

    // Mock API delay
    await new Promise(resolve => setTimeout(resolve, 1000))

    // Redirect to product page or listing success page
    await router.push('/products')
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

const handleImageUpdate = (data) => {
  if (data.error) {
    errors.images = data.message;
  } else {
    form.images = data.images;
    validateField('images');
  }
};

</script>
<template>
  <div class="new-listing-container">
    <div class="new-listing-form card">
      <h1>{{ t('newListing.createNewListing') }}</h1>

      <form @submit.prevent="createListing" ref="formRef">
        <!--title -->
        <BaseInputField
            id="title"
            :label="t('newListing.title')"
            v-model="form.title"
            :error="errors.title ? t(errors.title) : ''"
            :required="true"
            :maxLength="20"
            @input="debouncedValidate('title')"
        />

        <!--brief-description -->
        <BaseInputField id="briefDescription"
            :label="t('newListing.brief-description')"
            v-model="form.briefDescription"
            :required="false"
            :max-length="100"
        />

        <!--description -->
        <TextAreaField
            id="description"
            :label="t('newListing.description')"
            v-model="form.description"
            :error="errors.description ? t(errors.description) : ''"
            :required="true"
            :rows="5"
            :maxLength="2000"
            @input="debouncedValidate('description')"
        />

        <!-- For price field -->
        <NumberInputField
            id="price"
            :label="t('newListing.price')"
            v-model="form.price"
            :error="errors.price ? t(errors.price) : ''"
            :required="true"
            type="number"
            min="0"
            step="0.01"
            currencySymbol="kr"
            @input="debouncedValidate('price')"
        />

        <SelectField
            id="category"
            :label="t('newListing.category')"
            v-model="form.category"
            :error="errors.category ? t(errors.category) : ''"
            :required="true"
            :options="categories"
            :placeholder="t('newListing.selectCategory')"
            translationPrefix="categories"
            @change="validateField('category')"
        />

        <!-- Condition -->
        <RadioButtonGroup
            id="condition"
            :label="t('newListing.condition')"
            v-model="form.condition"
            :options="conditions"
            translationPrefix="conditions"
        />

        <ImageUploader
            :label="t('newListing.images')"
            :images="form.images"
            :error="errors.images ? t(errors.images) : ''"
            :required="true"
            :maxImages="5"
            @update:images="handleImageUpdate"
        />

        <!-- For postal code field -->
        <NumberInputField
            id="postalCode"
            :label="t('newListing.postalCode')"
            v-model="form.postalCode"
            :error="errors.postalCode ? t(errors.postalCode) : ''"
            :required="true"
            :maxLength="4"
            :additionalText="postalCity"
            @input="debouncedValidate('postalCode')"
        />

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
  margin: 0 auto;
  padding: 3rem 1rem;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 80vh;
}

.new-listing-form {
  padding: 3em;
  border-radius: 12px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
  background-color: var(--bg-color, white);
  width: 100%;
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: var(--text-color, #333);
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

select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  transition: all 0.2s;
  font-size: 1rem;
  background-color: var(--bg-color, white);
  color: var(--text-color, #333);
}

textarea:focus, select:focus {
  border-color: #4f46e5;
  outline: none;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.2);
}

input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.image-preview img {
  width: 100%;
  height: 120px;
  object-fit: cover;
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
  .new-listing-container {
    padding: 1rem;
    min-height: auto;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .btn {
    width: 100%;
  }
}
</style>