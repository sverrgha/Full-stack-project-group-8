<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = reactive({
  name: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

// Track errors when register is clicked
const errors = reactive({
  name: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const register = () => {
  clearErrors()

  // Validation error messages showing when register is clicked
  if (!form.name.trim()) {
    errors.name = 'Name is required.'
  }

  if (!/\S+@\S+\.\S+/.test(form.email)) {
    errors.email = 'Enter a valid email.'
  }

  if (!/^[0-9]+$/.test(form.phone)) {
    errors.phone = 'Phone number must contain digits only.'
  }

  if (form.password.length < 6) {
    errors.password = 'Password must be at least 6 characters.'
  }

  if (form.password !== form.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match.'
  }

  // If no errors, proceed
  const hasErrors = Object.values(errors).some(msg => msg !== '')
  if (hasErrors) return

  console.log('Registering:', form)
  router.push('/login')
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
    <h1>Register</h1>
    <form @submit.prevent="register">
      <!-- Name -->
      <div class="form-group">
        <label for="name">Name</label>
        <input type="text" id="name" v-model="form.name"
            :class="{ invalid: errors.name }"
        />
        <p v-if="errors.name" class="error-message">{{ errors.name }}</p>
      </div>

      <!-- Email -->
      <div class="form-group">
        <label for="email">Email</label>
        <input type="email" id="email" v-model="form.email"
            :class="{ invalid: errors.email }"
        />
        <p v-if="errors.email" class="error-message">{{ errors.email }}</p>
      </div>

      <!-- Phone -->
      <div class="form-group">
        <label for="phone">Phone number</label>
        <input type="text" id="phone" v-model="form.phone"
            :class="{ invalid: errors.phone }"
        />
        <p v-if="errors.phone" class="error-message">{{ errors.phone }}</p>
      </div>

      <!-- Password -->
      <div class="form-group">
        <label for="password">Password</label>
        <input type="password" id="password" v-model="form.password"
            :class="{ invalid: errors.password }"
        />
        <p v-if="errors.password" class="error-message">{{ errors.password }}</p>
      </div>

      <!-- Confirm Password -->
      <div class="form-group">
        <label for="confirmPassword">Confirm Password</label>
        <input type="password" id="confirmPassword" v-model="form.confirmPassword"
            :class="{ invalid: errors.confirmPassword }"
        />
        <p v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</p>
      </div>

      <!-- Register Button -->
      <button type="submit" class="btn primary">
        Register
      </button>

      <!-- Link to Login -->
      <p class="login-text">
        Already have an account?
        <span @click="goToLogin">Login here</span>
      </p>
    </form>
  </div>
</template>

<style scoped>
.register-form {
  max-width: 400px;
  margin: 5vh auto;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
  background-color: #ffffff;
  font-family: 'Segoe UI', sans-serif;
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
