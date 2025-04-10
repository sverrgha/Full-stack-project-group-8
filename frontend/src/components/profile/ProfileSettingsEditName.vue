<!-- ProfileSettingsEditName.vue - The component for editing user name -->

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '../../stores/auth'
import BaseInputField from '../form/BaseInputField.vue'
import { userService } from '../../services/userService'

const { t } = useI18n()
const auth = useAuthStore()

const firstname = ref('')
const lastname = ref('')
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const emit = defineEmits(['saveChanges'])

onMounted(async () => {
  await fetchUserData()
})

// Fetch user data from API
const fetchUserData = async () => {
  try {
    loading.value = true
    const userId = auth.user?.id

    if (!userId) {
      console.error('No user ID available')
      return
    }

    const response = await userService.getUserById(userId)
    firstname.value = response.data.firstname || ''
    lastname.value = response.data.lastname || ''
  } catch (error) {
    errorMsg.value = t('profileSettingsEditName.fetchError') || 'Failed to fetch user data'
  } finally {
    loading.value = false
  }
}

// Handle input changes
const handleSave = async () => {
  try {
    loading.value = true

    const userId = auth.user?.id
    if (!userId) {
      // Emit error event instead of handling locally
      emit('error', t('profileSettingsEditName.noUserId'))
      return
    }

    await userService.updateUser(userId, {
      firstname: firstname.value,
      lastname: lastname.value
    })

    // Emit success event
    emit('saveChanges')
  } catch (error) {
    emit('error', t('profileSettingsEditName.saveError'))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="settings-section">
    <h2>{{ t('profileSettingsEditName.h2') }}</h2>

    <!-- Error message -->
    <div v-if="errorMsg" class="error-message">
      {{ errorMsg }}
    </div>

    <!-- Success message -->
    <div v-if="successMsg" class="success-message">
      {{ successMsg }}
    </div>

    <!-- Input field for first name -->
    <div class="input-container">
      <BaseInputField
          id="firstname-input"
          :label="t('registerForm.firstname')"
          v-model="firstname"
          placeholder="Enter your first name"
          class="input-field"
      />

      <!-- Input field for last name -->
      <BaseInputField
          id="lastname-input"
          :label="t('registerForm.lastname')"
          v-model="lastname"
          placeholder="Enter your last name"
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

.success-message {
  background-color: #e8f5e9;
  color: #2e7d32;
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