<!-- ProfileInfo.vue -->
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { userService } from '../../services/userService'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const auth = useAuthStore()
const { t } = useI18n()

const name = ref('Loading...')
const loading = ref(true)
const isAdmin = ref(false)

// Navigate to profile settings page
const settings = () => router.push('/profile/settings')

// Navigate to admin dashboard
const goToAdminDashboard = () => router.push('/admin')

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

      // Set admin status from API response
      isAdmin.value = Boolean(response.data.admin)
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
    <!-- Settings controls -->
    <div class="settings-controls">
      <!-- Admin button - only shown for admins -->
      <button v-if="isAdmin" class="admin-toggle" @click="goToAdminDashboard">
        <img src="../../assets/admin.svg" alt="" class="admin-icon" />
        {{ t('admin.dashboard') || 'Admin' }}
      </button>

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

.settings-controls {
  position: absolute;
  top: 1rem;
  right: 1rem;
  display: flex;
  align-items: center;
  gap: 1rem;
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

.admin-toggle {
  background-color: lightgrey;
  color: black;
  border: none;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.admin-toggle:hover {
  background-color: darkgrey;
}

.admin-icon {
  width: 22px;
  height: 22px;
}

.admin-header h2 {
  margin: 0;
  font-size: 1.4rem;
  color: #333;
}
</style>