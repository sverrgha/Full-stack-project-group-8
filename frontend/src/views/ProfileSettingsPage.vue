<!-- ProfileSettingsPage.vue - Main page that uses individual profile settings components -->

<script setup>
import { ref, computed } from 'vue'
import ProfileSettingsSidebar from "../components/profile/ProfileSettingsSidebar.vue"
import ProfileSettingsEditName from "../components/profile/ProfileSettingsEditName.vue"
import ProfileSettingsEditPhoneNr from "../components/profile/ProfileSettingsEditPhoneNr.vue";
import { useAuthStore } from '../stores/auth'
import { useI18n } from 'vue-i18n'
import ProfileSettingsEditPassword from "../components/profile/ProfileSettingsEditPassword.vue";
import ProfileSettingsEditMail from "../components/profile/ProfileSettingsEditMail.vue";

const authStore = useAuthStore()
const { t } = useI18n()

// Reactive reference to track the active section, default is 'name'
const activeSection = ref('name')

// Message states
const errorMessage = ref('')
const successMessage = ref('')

//handle logout
const handleLogout = () => {
  authStore.logout()
}

// Use computed property to determine current active component
const activeComponent = computed(() => activeSection.value)

// Function to change the active section
const changeSection = (section) => {
  clearMessages()
  activeSection.value = section
}

// Clear all messages
const clearMessages = () => {
  successMessage.value = ''
  errorMessage.value = ''
}

// Function to handle successful changes
const handleSaveChanges = () => {
  clearMessages()

  if (activeSection.value === 'name') {
    successMessage.value = t('editProfile.saveNameSuccess')
  } else {
    successMessage.value = t('editProfile.saveSuccess')
  }

  setTimeout(() => {
    successMessage.value = ''
  }, 3000)
}

// Function to handle error from child components
const handleError = (message) => {
  clearMessages()
  errorMessage.value = message

  // Auto-clear error message after a few seconds
  setTimeout(() => {
    errorMessage.value = ''
  }, 3000)
}
</script>

<template>
  <div class="settings-container">

    <div class="header">
      <h1 class="settings-title">Profile Settings</h1>
      <button class="logout-button" @click="handleLogout">Logout</button>
    </div>

    <div class="settings-content">
      <!-- Using the ProfileSettings component only for layout/navigation -->
      <ProfileSettingsSidebar
          :activeSection="activeSection"
          @change-section="changeSection"
      />

      <!-- Main content area - changes based on active section -->
      <div class="settings-main">
        <!-- Global messages area -->
        <div v-if="successMessage" class="message success-message" role="status">
          {{ successMessage }}
        </div>
        <div v-if="errorMessage" class="message error-message" role="alert">
          {{ errorMessage }}
        </div>

        <!-- Name edit section -->
        <ProfileSettingsEditName
            v-if="activeComponent === 'name'"
            @saveChanges="handleSaveChanges"
            @error="handleError"
        />

        <!-- Mail edit section -->
        <ProfileSettingsEditMail
            v-if="activeComponent === 'mail'"
            @saveChanges="handleSaveChanges"
            @error="handleError"
        />

        <!-- Phone number edit section -->
        <ProfileSettingsEditPhoneNr
            v-if="activeComponent === 'phoneNr'"
            @saveChanges="handleSaveChanges"
            @error="handleError"
        />

        <!-- Password edit section -->
        <ProfileSettingsEditPassword
            v-if="activeComponent === 'password'"
            @saveChanges="handleSaveChanges"
            @error="handleError"
        />

      </div>
    </div>
  </div>
</template>

<style scoped>
.settings-container {
  max-width: 1000px;
  min-height: 700px;
  margin: 0 auto;
  padding: 2rem;
  box-sizing: border-box;
  font-family: 'Segoe UI', sans-serif;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.settings-title {
  color: #333;
  font-size: 2rem;
  margin: 0;
}

.logout-button {
  background-color: #f56565;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 0.6rem 1.2rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.logout-button:hover {
  background-color: #e53e3e;
}

.settings-content {
  display: flex;
  gap: 2rem;
  min-height: 400px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.08);
}

.settings-main {
  flex: 1;
  background-color: white;
  min-width: 500px;
  min-height: 450px;
  position: relative;
  display: flex;
  flex-direction: column;
}

.message {
  position: absolute;
  top: 0.5rem;
  left: 1rem;
  right: 1rem;
  z-index: 10;
  padding: 0.75rem 1rem;
  border-radius: 6px;
  font-weight: 500;
  font-size: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  animation: fadeIn 0.3s ease-in-out, fadeout 0.3s 2.7s ease-in-out;
}

.success-message {
  background-color: #f0fff4;
  color: #2f855a;
  border-left: 4px solid #48bb78;
}

.error-message {
  background-color: #fff5f5;
  color: #c53030;
  border-left: 4px solid #f56565;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeout {
  from { opacity: 1; }
  to { opacity: 0; }
}

@media (max-width: 968px) {
  .settings-content {
    flex-direction: column;
    gap: 1rem;
  }

  .settings-main {
    padding: 1.5rem;
    min-width: auto;
  }

  .settings-title {
    font-size: 1.8rem;
  }
}

@media (max-width: 676px) {
  .settings-container {
    padding: 1rem;
    max-width: 100%;
  }

  .header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .logout-button {
    align-self: flex-end;
  }

  .settings-main {
    padding: 1rem;
    min-width: auto;
  }

  .settings-title {
    font-size: 1.5rem;
  }

  .message {
    left: 0.5rem;
    right: 0.5rem;
    padding: 0.5rem 0.75rem;
  }
}

@media (max-width: 400px) {
  .settings-container {
    max-width: 100%;
  }
}
</style>