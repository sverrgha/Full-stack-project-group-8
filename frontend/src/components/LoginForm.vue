<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const email = ref('')
const password = ref('')

const router = useRouter()

const login = () => {
  console.log('Logging in:', email.value, password.value)
}

const register = () => {
  router.push('/register')
}
</script>

<template>
  <div class="login-form">
    <h1>{{ t('loginForm.login') }}</h1>
    <form @submit.prevent="login">
      <div class="form-group">
        <label for="email">{{ t('loginForm.email') }}</label>
        <input type="email" id="email" v-model="email" required />
      </div>
      <div class="form-group">
        <label for="password">{{ t('loginForm.password') }}</label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <button type="submit" class="btn primary">{{ t('loginForm.login') }}</button>

      <p class="register-text">
        {{ t('loginForm.notRegistered') }}
        <span @click="register">{{ t('loginForm.register')}}</span>
      </p>
    </form>
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
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #333;
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
