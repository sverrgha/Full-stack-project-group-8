<!-- ProfileInfo.vue - The upper part of the ProfilePage, including profile picture
 name, biography and settings icon-->

<script setup>
import { useRouter } from 'vue-router'

//Initialize router
const router = useRouter()

//Placeholders to profile text
const name = 'John Doe'
const biography = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.'

//Navigate to profile settings page
const settings = () => router.push('/profile/settings')
</script>

<template>
  <div class="profile-info card">
    <!-- Settings icon -->
    <div class="settings-icon-wrapper">
      <img src="../assets/settings.png" alt="Settings" @click="settings" class="settings-icon" />
    </div>

    <!-- Profile picture, name and biography -->
    <div class="profile-content">

      <!-- Profile picture -->
      <div class="profile-image">
        <img src="../assets/user.png" alt="Profile Image" />
      </div>

      <!-- Name and biography -->
      <div class="profile-text">
        <h1>{{ name }}</h1>
        <p>{{ biography }}</p>
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