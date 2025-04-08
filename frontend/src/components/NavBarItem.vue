<script setup>
import { defineProps, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from "vue-i18n";

// Import all icons
import messageIcon from '../assets/message.svg';
import addIcon from '../assets/addIcon.svg';
import bellIcon from '../assets/bellIcon.svg';
import productIcon from '../assets/Products.svg';
import userIcon from '../assets/user.svg';
import adminIcon from '../assets/admin.svg';

const { t } = useI18n();
const route = useRoute();
const props = defineProps({
  text: {
    type: String,
    required: true
  },
  to: {
    type: [String, Object],
    required: true
  },
  isActive: {
    type: Boolean,
    default: false
  }
});

// Create a map of text keys to icons
const iconMap = {
  'admin': adminIcon,
  'products': productIcon,
  'notifications': bellIcon,
  'newListing': addIcon,
  'messages': messageIcon,
  'profile': userIcon
};

// Get the appropriate icon based on text
const iconSrc = computed(() => {
  return iconMap[props.text] || productIcon; // Default to productIcon if not found
});

const isLinkActive = computed(() => {
  return props.isActive || route.path === (typeof props.to === 'string' ? props.to : props.to.path);
});
</script>

<template>
  <li>
    <router-link :to="to" :class="{'active': isLinkActive}">
      <img :src="iconSrc" :alt="text" />
      <span>{{ t("nav." + text) }}</span>
    </router-link>
  </li>
</template>

<style scoped>
a {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: black;
  font-size: 1rem;
  padding: 8px 15px;
  border-radius: 8px;
  transition: background 0.2s;
  white-space: nowrap;
}

img {
  width: 20px;
  height: 20px;
  margin-right: 8px;
}

a.active {
  background: #f5f5f5;
  border: 2px solid blue;
}

a:hover {
  background: #f0f0f0;
}
</style>