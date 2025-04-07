<!-- ProfileSettingsEditName.vue - The component for editing user name -->

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import BaseInputField from '../form/BaseInputField.vue' // Import the BaseInputField component

const { t } = useI18n()

const props = defineProps({
  userData: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && typeof value.name === 'string'
    }
  }
})

const emit = defineEmits(['update:userData', 'saveChanges'])

// Computed property for current name value
const name = computed({
  get: () => props.userData.name || '',
  set: (newValue) => {
    const newUserData = {
      ...props.userData,
      name: newValue
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
    <h2>{{ t('profileSettingsEditName.h2') }}</h2>
    <div class="input-container">
      <BaseInputField
          id="name-input"
          :label="t('profileSettingsEditName.name')"
          v-model="name"
          placeholder="Enter your first- and lastname"
          class="input-field"
      />
    </div>

    <div class="button-container">
      <button class="save-button" @click="handleSave" type="button">
        {{ t('profileSettingsEditName.save') }}
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

.input-field {
  margin-bottom: clamp(1rem, 3vw, 1.5rem);
  width: 100%;
  max-width: min(500px, 90%);
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