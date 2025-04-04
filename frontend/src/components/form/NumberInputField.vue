<!-- NumberInputField.vue -->
<script setup>
import { computed } from 'vue';

const props = defineProps({
  id: {
    type: String,
    required: true
  },
  label: {
    type: String,
    required: true
  },
  modelValue: {
    type: [String, Number],
    default: ''
  },
  type: {
    type: String,
    default: 'number'
  },
  required: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: ''
  },
  maxLength: {
    type: Number,
    default: null
  },
  currencySymbol: {
    type: String,
    default: ''
  },
  min: {
    type: [String, Number],
    default: null
  },
  step: {
    type: [String, Number],
    default: null
  },
  additionalText: {
    type: String,
    default: ''
  },
  maxWidth: {
    type: String,
    default: '150px'
  }
});

const emit = defineEmits(['update:modelValue', 'input']);

const handleInput = (event) => {
  emit('update:modelValue', event.target.value);
  emit('input', event);
};
</script>

<template>
  <div class="form-group">
    <label :for="id">
      {{ label }} <span v-if="required" class="required">*</span>
    </label>
    <div :class="{'postalcode-input-and-city': additionalText, 'number-wrapper': true}">
      <div class="number-input" :style="{ maxWidth }">
        <input
            :type="type"
            :id="id"
            :value="modelValue"
            @input="handleInput"
            :class="{ invalid: error }"
            :aria-invalid="!!error"
            :aria-describedby="error ? `${id}-error` : null"
            :maxlength="maxLength"
            :min="min"
            :step="step"
            v-bind="$attrs"
        />
        <span v-if="currencySymbol" class="currency-symbol">{{ currencySymbol }}</span>
      </div>
      <span v-if="additionalText" class="additional-text">{{ additionalText }}</span>
    </div>
    <p v-if="error" :id="`${id}-error`" class="error-message">{{ error }}</p>
  </div>
</template>

<style scoped>
.form-group {
  margin-bottom: var(--space-lg, 1.5rem);
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--text-color, #444);
  font-weight: 600;
}

.required {
  color: #e53e3e;
  margin-left: 2px;
}

.number-wrapper {
  display: flex;
  align-items: center;
}

.number-input {
  position: relative;
  display: flex;
  align-items: center;
}

.postalcode-input-and-city {
  position: relative;
  display: flex;
  align-items: center;
  max-width: 300px;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  transition: all 0.2s;
  font-size: 1rem;
  background-color: var(--bg-color, white);
  color: var(--text-color, #333);
}

input:focus {
  border-color: #4f46e5;
  outline: none;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.2);
}

input.invalid {
  border-color: #e53e3e;
}

input.invalid:focus {
  box-shadow: 0 0 0 3px rgba(229, 62, 62, 0.2);
}

.currency-symbol {
  position: absolute;
  right: 10px;
  font-weight: bold;
}

.additional-text {
  margin-left: 10px;
  color: #666;
  white-space: nowrap;
}

.error-message {
  color: #e53e3e;
  font-size: 0.85rem;
  margin-top: 0.25rem;
  animation: fadeIn 0.2s;
}

/* Remove spinner from number inputs */
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

input[type="number"] {
  -moz-appearance: textfield;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>