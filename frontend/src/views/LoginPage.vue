<script setup>
import { ref } from 'vue'
import LoginForm from '../components/LoginForm.vue'
import RegisterForm from '../components/RegisterForm.vue'

const isRegistering = ref(false)

const toggleForm = () => {
  isRegistering.value = !isRegistering.value
}
</script>

<template>
  <div class="login-page">
    <component :is="isRegistering ? RegisterForm : LoginForm" />
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #f9fafb;
  padding: 2rem;
}

.form-toggle span {
  color: #4f46e5;
  font-weight: 600;
  cursor: pointer;
  text-decoration: underline;
}

.form-toggle span:hover {
  color: #3730a3;
}
</style>
