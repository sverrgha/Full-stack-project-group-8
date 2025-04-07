<script setup>
import {reactive, ref} from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from "vue-i18n";
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const { t } = useI18n()
const authStore = useAuthStore()
const isSubmitting = ref(false)

const form = reactive({
  firstname: '',
  lastname: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const errors = reactive({
  firstname: '',
  lastname: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  api: ''
})

const register = async () => {
  clearErrors()

  // Validation error messages showing when register is clicked
  if (!form.firstname.trim()) {
    errors.firstname = 'registerForm.firstnameRequired'
  }

  if (!form.lastname.trim()) {
    errors.lastname = 'registerForm.lastnameRequired'
  }

  if (!/\S+@\S+\.\S+/.test(form.email)) {
    errors.email = 'registerForm.invalidEmail'
  }

  if (!/^[0-9]+$/.test(form.phone)) {
    errors.phone = 'registerForm.invalidPhoneNumber'
  }

  if (form.password.length < 8) {
    errors.password = 'registerForm.passwordLength'
  }

  if (form.password !== form.confirmPassword) {
    errors.confirmPassword = 'registerForm.passwordsMustMatch'
  }

  const hasErrors = Object.values(errors).some(msg => msg !== '')
  if (hasErrors) return

  try {
    isSubmitting.value = true;

    const userData = {
      email: form.email,
      password: form.password,
      firstname: form.firstname,
      lastname: form.lastname,
      phoneNumber: form.phone
    }

    await authStore.register(userData);

    console.log('Registering:', form)
    router.push('/login')
  } catch (error) {
    errors.api = error.response?.data?.message || 'registerForm.registrationFailed'
  } finally {
    isSubmitting.value = false;
  }
}

const clearErrors = () => {
  for (const key in errors) {
    errors[key] = ''
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="register-form">
    <h1>{{ t('registerForm.register') }}</h1>

    <!-- Display API errors if any -->
    <div v-if="errors.api" class="api-error">
      {{ t(errors.api) }}
    </div>

    <form @submit.prevent="register">
      <!-- First Name -->
      <div class="form-group">
        <label for="firstname">{{ t('registerForm.firstname') }}</label>
        <input type="text" id="firstname" v-model="form.firstname"
               :class="{ invalid: errors.firstname }"
        />
        <p v-if="errors.firstname" class="error-message">{{ t(errors.firstname) }}</p>
      </div>

      <!-- Last Name -->
      <div class="form-group">
        <label for="lastname">{{ t('registerForm.lastname') }}</label>
        <input type="text" id="lastname" v-model="form.lastname"
               :class="{ invalid: errors.lastname }"
        />
        <p v-if="errors.lastname" class="error-message">{{ t(errors.lastname) }}</p>
      </div>

      <!-- Email -->
      <div class="form-group">
        <label for="email">{{ t('registerForm.email') }}</label>
        <input type="email" id="email" v-model="form.email"
               :class="{ invalid: errors.email }"
        />
        <p v-if="errors.email" class="error-message">{{ t(errors.email) }}</p>
      </div>

      <!-- Phone -->
      <div class="form-group">
        <label for="phone">{{ t('registerForm.phoneNumber') }}</label>
        <input type="text" id="phone" v-model="form.phone"
               :class="{ invalid: errors.phone }"
        />
        <p v-if="errors.phone" class="error-message">{{ t(errors.phone) }}</p>
      </div>

      <!-- Password -->
      <div class="form-group">
        <label for="password">{{ t('registerForm.password') }}</label>
        <input type="password" id="password" v-model="form.password"
               :class="{ invalid: errors.password }"
        />
        <p v-if="errors.password" class="error-message">{{ t(errors.password) }}</p>
      </div>

      <!-- Confirm Password -->
      <div class="form-group">
        <label for="confirmPassword">{{  t('registerForm.confirmPassword') }}</label>
        <input type="password" id="confirmPassword" v-model="form.confirmPassword"
               :class="{ invalid: errors.confirmPassword }"
        />
        <p v-if="errors.confirmPassword" class="error-message">{{ t(errors.confirmPassword) }}</p>
      </div>

      <!-- Register Button with loading state -->
      <button type="submit" class="btn primary" :disabled="isSubmitting">
        {{ isSubmitting ? t('registerForm.registering') : t('registerForm.register') }}
      </button>

      <!-- Link to Login -->
      <p class="login-text">
        {{ t('registerForm.alreadyRegistered') }}
        <span @click="goToLogin">{{ t('registerForm.login') }}</span>
      </p>
    </form>
  </div>
</template>

<style scoped>
.register-form {
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

input.invalid {
  border-color: red;
}

.error-message {
  color: red;
  font-size: 0.85rem;
  margin-top: 0.25rem;
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

.login-text {
  text-align: center;
  margin-top: 1rem;
  font-size: 0.95rem;
  color: #666;
}

.login-text span {
  color: #4f46e5;
  font-weight: 600;
  cursor: pointer;
  text-decoration: underline;
}

.login-text span:hover {
  color: #3730a3;
}
</style>
