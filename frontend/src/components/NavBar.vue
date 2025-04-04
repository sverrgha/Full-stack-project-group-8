<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import messageIcon from '../assets/message.svg';
import addIcon from '../assets/addIcon.svg';
import bellIcon from '../assets/bellIcon.svg';
import productIcon from '../assets/Products.svg';
import logo from '../assets/logo.png';
import user from '../assets/user.svg';
import flagNo from '../assets/flag_no.jpg';
import flagEn from '../assets/flag_en.jpg';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';

const router = useRouter();


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

const toProfilePage = () => {
  router.push('/profile');
};
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
        <li>
          <a href="#" class="active">
            <img :src="productIcon" alt="Products" />
            <span>{{ t('nav.products') }}</span>
          </a>
        </li>
        <li>
          <a href="#">
            <img :src="bellIcon" alt="Notifications" />
            <span>{{ t('nav.notifications') }}</span>
          </a>
        </li>
        <li>
          <a href="#">
            <img :src="addIcon" alt="New Listing" />
            <span>{{ t('nav.newListing') }}</span>
          </a>
        </li>
        <li>
          <a href="#">
            <img :src="messageIcon" alt="Messages" />
            <span>{{ t('nav.messages') }}</span>
          </a>
        </li>
        <li>
          <a href="#" @click="toProfilePage">
            <div class="userLogo">
              <img :src="user" alt="User"/>
            </div>
          </a>
        </li>
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

.nav-links a.active {
  background: #f5f5f5;
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