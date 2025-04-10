<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { userService } from '../../services/userService'

// Initialize router
const router = useRouter()
const auth = useAuthStore()

// User data with reactive references
const name = ref('Loading...')
const biography = ref('')
const loading = ref(true)

// Navigate to profile settings page
const settings = () => router.push('/profile/settings')

// Fetch user data from API
const fetchUserData = async () => {
  try {
    loading.value = true
    const userId = auth.user.id

    // Check if user ID is available
    if (!userId) {
      console.error('No user ID available')
      name.value = 'Unknown User'
      return
    }

    const response = await userService.getUserById(userId)

    // Check if response data is valid
    if (response.data && typeof response.data === 'object') {
      if (response.data.firstname && response.data.lastname) {
        name.value = `${response.data.firstname} ${response.data.lastname}`
      } else {
        name.value = 'Unknown User'
      }
    } else {
      name.value = 'Unknown User'
    }
  } catch (error) {
    console.error('Error loading user:', error)
    name.value = 'Error loading user'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchUserData()
})
</script>

<template>
  <div class="profile-info card">
    <!-- Settings icon -->
    <div class="settings-icon-wrapper">
      <img src="../../assets/settings.png" alt="Settings" @click="settings" class="settings-icon" />
    </div>

    <div class="profile-content">
      <!-- Profile picture -->
      <div class="profile-image">
        <img src="../../assets/user.png" alt="Profile Image" />
      </div>

      <!-- Name-->
      <div class="profile-text">
        <div v-if="loading" class="loading">Loading user information...</div>
        <template v-else>
          <h1>{{ name }}</h1>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-content {
  display: flex;
  align-items: center;
  gap: 1rem;
  width: 100%;
}

.profile-text {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  text-align: left;
  width: 100%;
}

.profile-text h1,
.profile-text p {
  margin: 0;
  width: 100%;
}

.loading {
  color: #666;
  font-style: italic;
}

.profile-image img {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 50%;
  border: 3px solid black;
}

.settings-icon-wrapper {
  position: absolute;
  top: 1rem;
  right: 1rem;
}

.settings-icon {
  width: 28px;
  height: 28px;
  cursor: pointer;
  opacity: 0.7;
  transition: transform 0.5s ease, opacity 0.2s ease;
}

.settings-icon:hover {
  opacity: 1;
  transform: rotate(90deg);
}
</style>