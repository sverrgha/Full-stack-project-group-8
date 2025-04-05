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
    type: String,
    default: ''
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
  rows: {
    type: [String, Number],
    default: 5
  }
});

const emit = defineEmits(['update:modelValue', 'input']);

const currentLength = computed(() => {
  return String(props.modelValue || '').length;
});

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
    <div class="input-wrapper">
      <textarea
          :id="id"
          :value="modelValue"
          @input="handleInput"
          :class="{ invalid: error }"
          :aria-invalid="!!error"
          :aria-describedby="error ? `${id}-error` : null"
          :rows="rows"
          :maxlength="maxLength"
          v-bind="$attrs"
      ></textarea>
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

.required {
  color: #e53e3e;
  margin-left: 2px;
}

.input-wrapper {
  position: relative;
}

textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  transition: all 0.2s;
  font-size: 1rem;
  background-color: var(--bg-color, white);
  color: var(--text-color, #333);
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
  line-height: 1.5;
}

textarea:focus {
  border-color: #4f46e5;
  outline: none;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.2);
}

textarea.invalid {
  border-color: #e53e3e;
}

textarea.invalid:focus {
  box-shadow: 0 0 0 3px rgba(229, 62, 62, 0.2);
}

.char-count {
  position: absolute;
  right: 10px;
  bottom: 10px;
  font-size: 0.8rem;
  color: #666;
  background-color: rgba(255, 255, 255, 0.8);
  padding: 2px 5px;
  border-radius: 4px;
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