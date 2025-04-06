<!-- ProfileSettingsSideBar.vue - Sidebar navigation component -->

<script setup>
import { defineProps, defineEmits, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const props = defineProps({
  activeSection: {
    type: String,
    required: true,
    validator: (value) => ['name', 'biography', 'profilePicture'].includes(value)
  }
})

const emit = defineEmits(['change-section'])

// Function to change the active section
const changeSection = (section) => {
  emit('change-section', section)
}

// Navigation items with proper translations - made reactive with computed()
const navItems = computed(() => [
  { id: 'name', label: t('profileSettingsSideBar.name') },
  { id: 'biography', label: t('profileSettingsSideBar.biography') },
  { id: 'profilePicture', label: t('profileSettingsSideBar.profilePicture') }
])
</script>

<template>
  <!-- Sidebar navigation -->
  <div class="settings-sidebar">
    <!-- Generate buttons from navigation items array -->
    <button
        v-for="item in navItems"
        :key="item.id"
        :class="['sidebar-button', { active: activeSection === item.id }]"
        @click="changeSection(item.id)"
    >
      {{ item.label }}
    </button>
  </div>
</template>

<style scoped>
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

@media (max-width: 968px) {
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
}

@media (max-width: 676px) {
  .settings-sidebar {
    padding: 0.75rem 0.5rem;
  }

  .sidebar-button {
    padding: 0.75rem 0.5rem;
    font-size: 0.9rem;
    min-width: 100px;
    margin: 0.2rem;
  }
}

@media (max-width: 400px) {
  .sidebar-button {
    width: 100%;
    max-width: none;
    margin: 0.15rem 0;
  }
}
</style>