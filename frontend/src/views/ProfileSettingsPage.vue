<!-- ProfileSettingsPage.vue - Main page that uses individual profile settings components -->

<script setup>
import { ref, computed } from 'vue'
import ProfileSettingsSidebar from "../components/profile/ProfileSettingsSidebar.vue"
import ProfileSettingsEditName from "../components/profile/ProfileSettingsEditName.vue"
import ProfileSettingsEditBiography from "../components/profile/ProfileSettingsEditBiography.vue"
import ProfileSettingsEditPicture from "../components/profile/ProfileSettingsEditPicture.vue"

// Reactive reference to track the active section, default is 'name'
const activeSection = ref('name')

// Placeholder user data
const userData = ref({
  name: 'John Doe',
  biography: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. ' +
      'Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
  profilePicture: '../assets/user.png'
})

// Message states
const errorMessage = ref('')
const successMessage = ref('')

// Function to validate the name
const isValidName = (name) => {
  const regex = /^[A-Za-z\s]+$/
  return regex.test(name.trim())
}

// Use computed property to determine current active component
const activeComponent = computed(() => activeSection.value)

// Function to change the active section
const changeSection = (section) => {
  // Clear any messages when changing sections
  clearMessages()
  activeSection.value = section
}

// Clear all messages
const clearMessages = () => {
  successMessage.value = ''
  errorMessage.value = ''
}

// Function to save changes, with validation for different sections
const saveChanges = () => {
  // Clear any previous messages
  clearMessages()

  if (activeSection.value === 'name') {
    if (!isValidName(userData.value.name)) {
      errorMessage.value = 'Name can only contain letters and spaces.'

      // Auto-clear error message after 3 seconds
      setTimeout(() => {
        errorMessage.value = ''
      }, 3000)

      return
    }
  }

  // Show success message
  successMessage.value = 'Changes saved successfully!'

  // Optionally clear success message after a few seconds
  setTimeout(() => {
    successMessage.value = ''
  }, 3000)
}

// Function to handle profile picture change with validation
const handleProfilePictureChange = (event) => {
  // Clear any previous messages
  clearMessages()

  const file = event.target.files[0]
  if (!file) return

  // Size validation only - format is restricted by the accept attribute
  const maxSizeInMB = 2
  if (file.size > maxSizeInMB * 1024 * 1024) {
    errorMessage.value = `File must be smaller than ${maxSizeInMB}MB.`

    // Auto-clear error message after 3 seconds
    setTimeout(() => {
      errorMessage.value = ''
    }, 3000)

    return
  }

  const reader = new FileReader()
  reader.onload = (e) => {
    userData.value = {
      ...userData.value,
      profilePicture: e.target.result
    }
  }
  reader.readAsDataURL(file)
}

// Update user data from child components
const updateUserData = (newUserData) => {
  userData.value = newUserData
}
</script>

<template>
  <div class="settings-container">
    <h1 class="settings-title">Profile Settings</h1>

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
            :userData="userData"
            @update:userData="updateUserData"
            @saveChanges="saveChanges"
        />

        <!-- Biography edit section -->
        <ProfileSettingsEditBiography
            v-if="activeComponent === 'biography'"
            :userData="userData"
            @update:userData="updateUserData"
            @saveChanges="saveChanges"
        />

        <!-- Profile Picture edit section -->
        <ProfileSettingsEditPicture
            v-if="activeComponent === 'profilePicture'"
            :userData="userData"
            :pictureError="errorMessage"
            @pictureChange="handleProfilePictureChange"
            @saveChanges="saveChanges"
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

.settings-title {
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
  font-size: 2rem;
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
    margin-bottom: 1.5rem;
  }
}

@media (max-width: 676px) {
  .settings-container {
    padding: 1rem;
    max-width: 100%;
  }

  .settings-main {
    padding: 1rem;
    min-width: auto;
  }

  .settings-title {
    font-size: 1.5rem;
    margin-bottom: 1rem;
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