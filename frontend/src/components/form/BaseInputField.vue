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
    default: 'text'
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
  hideLabel: {
    type: Boolean,
    default: false
  },
  iconSrc: {
    type: String,
    default: ''
  },
  iconAlt: {
    type: String,
    default: 'Icon'
  }
});

const emit = defineEmits(['update:modelValue', 'focus', 'blur', 'input']);

const currentLength = computed(() => {
  return String(props.modelValue || '').length;
});

const handleInput = (event) => {
  emit('update:modelValue', event.target.value);
  emit('input', event);
};

const handleFocus = (event) => {
  emit('focus', event);
};

const handleBlur = (event) => {
  emit('blur', event);
};
</script>

<template>
  <div class="form-group">
    <label :for="id" :class="{ 'sr-only': hideLabel }">
      {{ label }} <span v-if="required" class="required">*</span>
    </label>
    <div class="input-wrapper">
      <!-- Icon from props -->
      <img v-if="iconSrc" :src="iconSrc" :alt="iconAlt" class="input-icon" />

      <!-- Icon from slot -->
      <slot name="icon"></slot>

      <input
          :type="type"
          :id="id"
          :value="modelValue"
          @input="handleInput"
          @focus="handleFocus"
          @blur="handleBlur"
          :class="{
            invalid: error,
            'has-icon': iconSrc || $slots.icon
          }"
          :aria-invalid="!!error"
          :aria-describedby="error ? `${id}-error` : null"
          :maxlength="maxLength"
          v-bind="$attrs"
      />
      <span v-if="maxLength" class="char-count">
        {{ currentLength }}/{{ maxLength }}
      </span>
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

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

.required {
  color: #e53e3e;
  margin-left: 2px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  width: 23px;
  height: 23px;
  pointer-events: none;
  z-index: 5;
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

input.has-icon {
  padding-left: 50px;
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

.char-count {
  position: absolute;
  right: 10px;
  bottom: 10px;
  font-size: 0.8rem;
  color: #666;
  pointer-events: none;
}

.error-message {
  color: #e53e3e;
  font-size: 0.85rem;
  margin-top: 0.25rem;
  animation: fadeIn 0.2s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>