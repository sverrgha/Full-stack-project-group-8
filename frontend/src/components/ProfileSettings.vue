<script setup>
import { ref, watch } from 'vue'

const activeSection = ref('name') // Default to 'name' section

const userData = ref({
  name: 'John Doe',
  bio: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
  profilePicture: '../assets/user.png'
})

// Function to change the active section
const changeSection = (section) => {
  activeSection.value = section
}

const nameError = ref('')
const pictureError = ref('')

// Function to validate the name
const isValidName = (name) => {
  const regex = /^[A-Za-z\s]+$/
  return regex.test(name.trim())
}

// Function to save changes (placeholder)
const saveChanges = () => {
  if (activeSection.value === 'name') {
    if (!isValidName(userData.value.name)) {
      nameError.value = 'Name can only contain letters and spaces.'
      return
    } else {
      nameError.value = ''
    }
  }

  alert('Changes saved successfully!')
}

// Live validation while typing name
watch(() => userData.value.name, (newVal) => {
  if (nameError.value && isValidName(newVal)) {
    nameError.value = ''
  }
})

const handleProfilePictureChange = (event) => {
  const file = event.target.files[0]
  if (!file) return

  const validTypes = ['image/jpeg', 'image/png', 'image/jpg']
  const maxSizeInMB = 2

  if (!validTypes.includes(file.type)) {
    pictureError.value = 'Only JPG and PNG images are allowed.'
    return
  }

  if (file.size > maxSizeInMB * 1024 * 1024) {
    pictureError.value = `File must be smaller than ${maxSizeInMB}MB.`
    return
  }

  pictureError.value = ''

  const reader = new FileReader()
  reader.onload = (e) => {
    userData.value.profilePicture = e.target.result
  }
  reader.readAsDataURL(file)
}

</script>

<template>
  <div class="settings-container">
    <h1 class="settings-title">Profile Settings</h1>

    <div class="settings-content">
      <!-- Sidebar navigation -->
      <div class="settings-sidebar">
        <button :class="['sidebar-button', { active: activeSection === 'name' }]"
                @click="changeSection('name')">First- and lastname
        </button>

        <button :class="['sidebar-button', { active: activeSection === 'bio' }]"
                @click="changeSection('bio')">Biography
        </button>

        <button :class="['sidebar-button', { active: activeSection === 'profilePicture' }]"
                @click="changeSection('profilePicture')">Profile Picture
        </button>
      </div>

      <!-- Main content area - changes based on active section -->
      <div class="settings-main">
        <!-- Name section -->
        <div v-if="activeSection === 'name'" class="settings-section">
          <h2>Update Your Name</h2>
          <div class="form-group">
            <label for="name-input">First- and lastname</label>
            <input
                id="name-input"
                type="text"
                v-model="userData.name"
                placeholder="Enter your name"
                :class="['form-input', { 'input-error': nameError }]"
            />
          </div>
          <p v-if="nameError" class="error-text">{{ nameError }}</p>

          <button class="save-button" @click="saveChanges">Save Changes</button>
        </div>

        <!-- Biography section -->
        <div v-if="activeSection === 'bio'" class="settings-section">
          <h2>Update Your Biography</h2>
          <div class="form-group">
            <label for="bio-input">Biography</label>
            <textarea
                id="bio-input"
                v-model="userData.bio"
                placeholder="Tell us about yourself"
                class="form-textarea"
                rows="5"
            ></textarea>
          </div>
          <button class="save-button" @click="saveChanges">Save Changes</button>
        </div>

        <!-- Profile Picture section -->
        <div v-if="activeSection === 'profilePicture'" class="settings-section">
          <h2>Update Profile Picture</h2>
          <div class="profile-picture-container">
            <div class="picture-upload-wrapper">
              <img :src="userData.profilePicture" alt="Current profile picture" class="current-picture" />
              <label for="picture-input" class="file-input-label">Change profile picture</label>
              <input
                  id="picture-input"
                  type="file"
                  accept="image/*"
                  class="file-input"
                  @change="handleProfilePictureChange"
              />
              <p v-if="pictureError" class="error-text">{{ pictureError }}</p>
            </div>
          </div>
          <button class="save-button" @click="saveChanges">Save Changes</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.settings-container {
  max-width: 1000px;
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

.settings-sidebar {
  width: 250px;
  background-color: #f5f5f5;
  padding: 1.5rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  flex-shrink: 0;
}

.sidebar-button {
  padding: 1rem;
  text-align: left;
  background: none;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.sidebar-button:hover {
  background-color: #e0e0e0;
}

.sidebar-button.active {
  background-color: #4f46e5;
  color: white;
  font-weight: 600;
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

.settings-section {
  padding: 2rem;
}

.settings-section h2 {
  color: #333;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
  width: 100%;
  max-width: 700px;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #555;
}

.form-input, .form-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
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
}

.error-text {
  color: red;
  font-size: 0.9rem;
  margin-top: 0.25rem;
}

.input-error {
  border-color: red;
}

.save-button:hover {
  background-color: #3c3799;
}

.current-picture {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 50%;
  border: 3px solid #eee;
  margin-bottom: 0.75rem;
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
  padding: 0.5rem;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
  text-align: center;
  text-decoration: none;
  font-size: 0.95rem;
}

.file-input-label:hover {
  color: #3c3799;
}

@media (max-width: 968px) {
  .settings-content {
    flex-direction: column;
    gap: 1rem;
  }

  .settings-sidebar {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
    padding: 1rem;
  }

  .sidebar-button {
    flex: 1 1 auto;
    max-width: 200px;
    min-width: 120px;
    text-align: center;
    margin: 0.25rem;
  }

  .settings-main {
    padding: 1.5rem;
    min-width: auto;
  }

  .settings-title {
    font-size: 1.8rem;
    margin-bottom: 1.5rem;
  }

  .form-group {
    max-width: 100%;
  }
}

@media (max-width: 676px) {
  .settings-container {
    padding: 1rem;
    max-width: 100%;
  }

  .settings-sidebar {
    padding: 0.75rem 0.5rem;
  }

  .sidebar-button {
    padding: 0.75rem 0.5rem;
    font-size: 0.9rem;
    min-width: 100px;
    margin: 0.2rem;
  }

  .settings-main {
    padding: 1rem;
    min-width: auto;
  }

  .settings-section {
    padding: 1rem;
  }

  .settings-section h2 {
    font-size: 1.3rem;
    margin-bottom: 1rem;
  }

  .form-input, .form-textarea {
    padding: 0.6rem;
  }

  .save-button {
    padding: 0.6rem 1.2rem;
    font-size: 0.9rem;
    width: 100%;
    max-width: 300px;
  }

  .settings-title {
    font-size: 1.5rem;
    margin-bottom: 1rem;
  }

  .current-picture {
    width: 120px;
    height: 120px;
  }

  .profile-picture-container {
    margin-bottom: 1rem;
  }
}

@media (max-width: 400px) {
  .settings-container {
    max-width: 100%;
  }

  .sidebar-button {
    width: 100%;
    max-width: none;
    margin: 0.15rem 0;
  }
}
</style>