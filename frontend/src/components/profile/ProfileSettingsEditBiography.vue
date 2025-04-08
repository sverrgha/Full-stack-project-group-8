<!-- ProfileSettingsEditBiography.vue - The component for editing user biography -->

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import TextAreaField from '../form/TextAreaField.vue' // Import the BaseTextareaField component

const { t } = useI18n()

const props = defineProps({
  userData: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && typeof value.biography === 'string'
    }
  }
})

const emit = defineEmits(['update:userData', 'saveChanges'])

// Computed property for current biography value with getter/setter
const biography = computed({
  get: () => props.userData.biography || '',
  set: (newValue) => {
    const newUserData = {
      ...props.userData,
      biography: newValue
    }
    emit('update:userData', newUserData)
  }
})

// Trigger save changes
const handleSave = () => {
  emit('saveChanges')
}
</script>

<template>
  <div class="settings-section">
    <h2>{{ t('profileSettingsEditBiography.h2') }}</h2>
    <div class="input-container">
      <TextAreaField
          id="bio-input"
          :label="t('profileSettingsEditBiography.biography')"
          v-model="biography"
          placeholder="Tell us about yourself"
          :rows="5"
          class="textarea-field"
      />
      <p class="input-help-text">{{ t('profileSettingsEditBiography.helpText') }}</p>
    </div>

    <div class="button-container">
      <button class="save-button" @click="handleSave" type="button">
        {{ t('profileSettingsEditBiography.save') }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.settings-section {
  padding: clamp(1rem, 5vw, 2rem);
  width: 100%;
  box-sizing: border-box;
}

.settings-section h2 {
  color: #333;
  margin-bottom: clamp(1rem, 3vw, 1.5rem);
  font-size: clamp(1.2rem, 3vw, 1.5rem);
  margin-top: clamp(1rem, 5vw, 2rem);
}

.input-container,
.button-container {
  width: 100%;
}

.textarea-field {
  margin-bottom: 0.5rem;
  width: 100%;
  max-width: min(700px, 90%);
}

.input-help-text {
  margin-top: 0.5rem;
  font-size: 0.85rem;
  color: #666;
  max-width: min(700px, 90%);
}

.save-button {
  background-color: #4f46e5;
  color: white;
  border: none;
  padding: clamp(0.6rem, 2vw, 0.75rem) clamp(1.2rem, 3vw, 1.5rem);
  border-radius: 8px;
  font-size: clamp(0.9rem, 2vw, 1rem);
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease;
  width: auto;
  max-width: min(300px, 100%);
}

.save-button:hover {
  background-color: #3c3799;
}

.save-button:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.4);
}

@media (max-width: 480px) {
  .save-button {
    width: 100%;
  }
}
</style>