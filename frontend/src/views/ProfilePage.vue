<script setup>
import ProfileInfo from '../components/ProfileInfo.vue'
import ProfilePosts from '../components/ProfilePosts.vue'
</script>

<template>
  <div class="profile-page">
    <ProfileInfo />
    <ProfilePosts />
  </div>
</template>

<style>
/* Global shared styles */
:root {
  --card-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
  --card-radius: 12px;
  --main-font: 'Segoe UI', sans-serif;
}

.profile-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
  font-family: var(--main-font);
}

.card {
  position: relative;
  border-radius: var(--card-radius);
  box-shadow: var(--card-shadow);
  padding: 2rem;
  margin-bottom: 2rem;
  width: 100%;
}

.profile-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.profile-posts {
  margin-top: 2rem;
  padding-bottom: 2rem;
}
</style>