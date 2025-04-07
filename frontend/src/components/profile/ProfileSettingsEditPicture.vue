<!-- ProfileSettingsEditPicture.vue - The component for editing profile picture -->

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const props = defineProps({
  userData: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && typeof value.profilePicture === 'string'
    }
  }
})

const emit = defineEmits(['update:userData', 'saveChanges', 'pictureChange'])

// Computed property for current profile picture
const profilePicture = computed(() => props.userData.profilePicture || '')

// Trigger save changes
const handleSave = () => {
  emit('saveChanges')
}

// Handle profile picture change with validation
const handleProfilePictureChange = (event) => {
  emit('pictureChange', event)
}
</script>

<template>
  <div class="settings-section">
    <h2>{{ t('profileSettingsEditPicture.h2') }}</h2>
    <div class="profile-picture-container">
      <div class="picture-upload-wrapper">
        <img
            :src="profilePicture"
            alt="Current profile picture"
            class="current-picture"
        />
        <label for="picture-input" class="file-input-label">{{ t('profileSettingsEditPicture.profilePicture') }}</label>
        <input
            id="picture-input"
            type="file"
            accept="image/jpeg,image/png,image/jpg"
            class="file-input"
            @change="handleProfilePictureChange"
        />
        <p class="help-text">
          {{ t('profileSettingsEditPicture.helpText') }}
        </p>
      </div>
    </div>
    <button class="save-button" @click="handleSave" type="button">{{ t('profileSettingsEditPicture.save') }}</button>
  </div>
</template>

<style scoped>
.settings-section {
  padding: 2rem;
  width: 100%;
}

.settings-section h2 {
  color: #333;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
  margin-top: 2rem;
}

.save-button {
  background-color: #4f46e5;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin: 0 auto;
  display: block;
}

.save-button:hover {
  background-color: #3c3799;
}

.save-button:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.4);
}

.help-text {
  font-size: 0.85rem;
  color: #666;
  margin-top: 0.5rem;
  text-align: center;
}

.current-picture {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 50%;
  border: 3px solid #eee;
  margin-bottom: 0.75rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.profile-picture-container {
  display: flex;
  justify-content: center;
  margin-bottom: 1.5rem;
}

.picture-upload-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.file-input {
  display: none;
}

.file-input-label {
  display: inline-block;
  color: #4f46e5;
  padding: 0.5rem 1rem;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
  text-align: center;
  text-decoration: none;
  font-size: 0.95rem;
  border: 1px solid #4f46e5;
  border-radius: 6px;
  background-color: white;
}

.file-input-label:hover {
  color: white;
  background-color: #4f46e5;
}

.file-input-label:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.4);
}

@media (max-width: 676px) {
  .settings-section {
    padding: 1rem;
  }

  .settings-section h2 {
    font-size: 1.3rem;
    margin-bottom: 1rem;
  }

  .save-button {
    padding: 0.6rem 1.2rem;
    font-size: 0.9rem;
    width: 100%;
    max-width: 300px;
  }

  .current-picture {
    width: 120px;
    height: 120px;
  }

  .profile-picture-container {
    margin-bottom: 1rem;
  }
}
</style>