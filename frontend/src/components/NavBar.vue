<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import logo from '../assets/logo.png';
import flagNo from '../assets/flag_no.jpg';
import flagEn from '../assets/flag_en.jpg';
import { useI18n } from 'vue-i18n';
import NavBarItem from "./NavBarItem.vue";

const isMenuOpen = ref(false);
const { locale, t } = useI18n();

const toggleLanguage = () => {
  locale.value = (locale.value === 'no' ? 'en' : 'no');
};


const closeMenu = (event) => {
  if (!event.target.closest('.nav-container')) {
    isMenuOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', closeMenu);
});

onUnmounted(() => {
  document.removeEventListener('click', closeMenu);
});
</script>

<template>
  <nav class="navbar">
    <div class="nav-container">
      <!-- Logo -->
      <div class="logo">
        <img :src="logo" alt="Logo" />
      </div>

      <!-- Mobile Menu Toggle Button -->
      <button class="menu-toggle" @click.stop="isMenuOpen = !isMenuOpen">
        ☰
      </button>

      <!-- Navigation Links -->
      <ul class="nav-links" :class="{ 'open': isMenuOpen }">
        <nav-bar-item to="/profile/settings" text="products"/>
        <nav-bar-item to="#" text="notifications"/>
        <nav-bar-item to="#" text="newListing"/>
        <nav-bar-item to="#" text="messages"/>
        <nav-bar-item to="/profile" text="profile"/>
        <li>
          <div class="language-toggle" @click="toggleLanguage">
            <img :src="locale === 'no' ? flagNo : flagEn"
                 :alt="locale === 'no' ? 'Switch to English' : 'Switch to Norwegian'"
                 id="language_image"/>
            <span>{{ t('nav.language') }}</span>
          </div>
        </li>
      </ul>
    </div>
  </nav>
  <div class="navbar-spacer"></div>
</template>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: white;
  box-shadow: 0 1px 0 #ddd;
  border-bottom: none;
  padding: 10px 20px;
}

.navbar-spacer {
  height: 60px;
}

.nav-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
}

.nav-links, div.language-toggle {
  list-style: none;
  display: flex;
  gap: 10px;
  transition: all 0.3s ease-in-out;
}

.nav-links a , div.language-toggle{
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

.nav-links img {
  width: 20px;
  height: 20px;
}

.nav-links a:hover, div.language-toggle:hover {
  background: #f0f0f0;
}

.userLogo img {
  width: 20px;
  height: 20px;
  vertical-align: middle;
}

.logo img {
  width: 150px;
  height: auto;
}

div.language-toggle {
  cursor: pointer;
}

#language_image {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 1px solid black;
  object-fit: cover;
}

/* Mobile Styles */
.menu-toggle {
  display: none;
  font-size: 24px;
  background: none;
  border: none;
  cursor: pointer;
}

@media (max-width: 910px) {
  .menu-toggle {
    display: block;
  }

  .nav-links {
    display: none;
    flex-direction: column;
    position: absolute;
    top: 60px;
    right: 0;
    background: white;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    width: 200px;
    padding: 10px;
    border-radius: 8px;
    z-index: 1001;
  }

  .nav-links.open {
    display: flex;
  }
}
</style>