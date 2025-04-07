<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '../stores/auth'

const { t } = useI18n()
const router = useRouter()
const authStore = useAuthStore()
const isSubmitting = ref(false)

const credentials = reactive({
  email: '',
  password: ''
})

const error = ref('')

const login = async () => {
  try {
    isSubmitting.value = true
    error.value = ''

    // Call the login method from auth store
    await authStore.login({
      email: credentials.email,
      password: credentials.password
    })
    // The store will handle the redirect after successful login
  } catch (err) {
    error.value = 'loginForm.loginFailed'
  } finally {
    isSubmitting.value = false
  }
}

const register = () => {
  router.push('/register')
}
</script>

<template>
  <div>
    <div class="login-form">
      <!-- Loading state -->
      <div v-if="isSubmitting" class="loading-container">
        <div class="loading-spinner"></div>
        <p>{{ t('loginForm.loggingIn') }}</p>
      </div>

      <!-- Normal form content -->
      <div v-else>
        <h1>{{ t('loginForm.login') }}</h1>

        <!-- Display API errors if any -->
        <div v-if="error" class="api-error">
          {{ t(error) }}
        </div>

        <form @submit.prevent="login">
          <div class="form-group">
            <label for="email">{{ t('loginForm.email') }}</label>
            <input type="email" id="email" v-model="credentials.email" required />
          </div>
          <div class="form-group">
            <label for="password">{{ t('loginForm.password') }}</label>
            <input type="password" id="password" v-model="credentials.password" required />
          </div>
          <button type="submit" class="btn primary">
            {{ t('loginForm.login') }}
          </button>

          <p class="register-text">
            {{ t('loginForm.notRegistered') }}
            <span @click="register">{{ t('loginForm.register')}}</span>
          </p>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-form {
  max-width: 400px;
  min-width: 300px;
  margin: 5vh auto;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
  background-color: #ffffff;
  min-height: 300px;
  display: flex;
  flex-direction: column;
}

.loading-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.loading-spinner {
  width: 3rem;
  height: 3rem;
  border: 4px solid rgba(79, 70, 229, 0.2);
  border-radius: 50%;
  border-top-color: #4f46e5;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 1rem;
}

.loading-container p {
  color: #4f46e5;
  font-weight: 600;
  font-size: 1.1rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #333;
}

.api-error {
  color: #e74c3c;
  background-color: #fde2e2;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #444;
  font-weight: 600;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  transition: border-color 0.2s;
  font-size: 1rem;
}

input:focus {
  border-color: #4f46e5;
  outline: none;
}

.btn.primary {
  width: 100%;
  padding: 0.75rem;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-top: 1rem;
}

.btn.primary:hover {
  background-color: #4338ca;
}

.register-text {
  text-align: center;
  margin-top: 1rem;
  font-size: 0.95rem;
  color: #666;
}

.register-text span {
  color: #4f46e5;
  font-weight: 600;
  cursor: pointer;
  text-decoration: underline;
}

.register-text span:hover {
  color: #3730a3;
}
</style>