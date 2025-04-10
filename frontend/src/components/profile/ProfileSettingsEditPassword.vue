<!-- ProfileSettingsEditPassword.vue - Component for changing password -->

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '../../stores/auth'
import BaseInputField from '../form/BaseInputField.vue'
import { userService } from '../../services/userService'

const { t } = useI18n()
const auth = useAuthStore()

const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const errorMsg = ref('')

const emit = defineEmits(['saveChanges', 'error'])

// Validate password strength
const isValidPassword = (password) => {
  return password && password.length >= 6
}

// Handle save changes
const handleSave = async () => {
  try {
    // Validate input fields
    if (!currentPassword.value) {
      emit('error', t('profileSettingsEditPassword.currentPasswordRequired'))
      return
    }

    if (!isValidPassword(newPassword.value)) {
      emit('error', t('registerForm.passwordLength'))
      return
    }

    if (newPassword.value !== confirmPassword.value) {
      emit('error', t('registerForm.passwordsMustMatch'))
      return
    }

    loading.value = true

    const userId = auth.user?.id
    if (!userId) {
      emit('error', t('profileSettingsEditName.noUserId'))
      return
    }

    // Update password in database
    await userService.updatePassword(userId, {
      currentPassword: currentPassword.value,
      newPassword: newPassword.value
    })

    // Clear input fields
    currentPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''

    // Show success message
    emit('saveChanges')

  } catch (error) {
    emit('error', t('profileSettingsEditPassword.saveError'))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="settings-section">
    <h2>{{ t('profileSettingsSideBar.password') }}</h2>

    <div v-if="errorMsg" class="error-message">
      {{ errorMsg }}
    </div>

    <div class="input-container">
      <BaseInputField
          id="current-password"
          :label="t('profileSettingsEditPassword.currentPassword')"
          v-model="currentPassword"
          type="password"
          class="input-field"
      />

      <BaseInputField
          id="new-password"
          :label="t('profileSettingsEditPassword.newPassword')"
          v-model="newPassword"
          type="password"
          class="input-field"
      />

      <BaseInputField
          id="confirm-password"
          :label="t('profileSettingsEditPassword.confirmPassword')"
          v-model="confirmPassword"
          type="password"
          class="input-field"
      />
    </div>

    <div class="button-container">
      <button
          class="save-button"
          @click="handleSave"
          type="button"
          :disabled="loading"
      >
        {{ loading ? (t('common.saving')) : (t('profileSettingsEditName.save')) }}
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

.save-button:disabled {
  background-color: #a5a5a5;
  cursor: not-allowed;
}

.error-message {
  background-color: #ffebee;
  color: #c62828;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 16px;
}

@media (max-width: 480px) {
  .save-button {
    width: 100%;
  }
}
</style>