import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import i18n from './i18n.js'
import { useAuthStore } from './stores/auth'

const pinia = createPinia()
const app = createApp(App)

app.use(router)
app.use(pinia)
app.use(i18n)

// Initialize auth state after Pinia is installed but before mounting
const authStore = useAuthStore()
authStore.initAuth()

app.mount('#app')