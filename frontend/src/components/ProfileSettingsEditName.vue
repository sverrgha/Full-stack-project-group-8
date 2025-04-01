<!-- ProfileSettingsEditName.vue - The component for editing user name -->

<script setup>
import { computed } from 'vue'

const props = defineProps({
  userData: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && typeof value.name === 'string'
    }
  }
})

const emit = defineEmits(['update:userData', 'saveChanges'])

// Computed property for current name value
const name = computed(() => props.userData.name || '')

// Update the name and emit the change
const updateName = (event) => {
  const newUserData = {
    ...props.userData,
    name: event.target.value
  }
  emit('update:userData', newUserData)
}

// Trigger save changes
const handleSave = () => {
  emit('saveChanges')
}
</script>

<template>
  <div class="settings-section">
    <h2>Update Your Name</h2>
    <div class="form-group">
      <label for="name-input">First- and lastname</label>
      <input
          id="name-input"
          type="text"
          :value="name"
          @input="updateName"
          placeholder="Enter your first- and lastname"
          class="form-input"
      />
    </div>

    <button class="save-button" @click="handleSave" type="button">Save Changes</button>
  </div>
</template>

<style scoped>
.settings-section {
  padding: 2rem;
  width: 100%;
}

.settings-section h2 {
  color: #333;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
  margin-top: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
  width: 100%;
  max-width: 700px;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #555;
}

.form-input {
  width: 90%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
}

.save-button {
  background-color: #4f46e5;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.save-button:hover {
  background-color: #3c3799;
}

.save-button:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.4);
}

@media (max-width: 676px) {
  .settings-section {
    padding: 1rem;
  }

  .settings-section h2 {
    font-size: 1.3rem;
    margin-bottom: 1rem;
  }

  .form-input {
    padding: 0.6rem;
  }

  .save-button {
    padding: 0.6rem 1.2rem;
    font-size: 0.9rem;
    width: 100%;
    max-width: 300px;
  }
}
</style>