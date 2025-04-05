<script setup>
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
  options: {
    type: Array,
    required: true
  },
  required: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: ''
  },
  optionLabelKey: {
    type: String,
    default: null
  },
  optionValueKey: {
    type: String,
    default: null
  },
  translationPrefix: {
    type: String,
    default: null
  }
});

const emit = defineEmits(['update:modelValue', 'change', 'input']);

const handleInput = (event) => {
  emit('update:modelValue', event.target.value);
  emit('input', event);
  emit('change', event);
};
</script>

<template>
  <div class="form-group">
    <label :for="id">
      {{ label }} <span v-if="required" class="required">*</span>
    </label>
    <select
        :id="id"
        :value="modelValue"
        @input="handleInput"
        @change="handleInput"
        :class="{ invalid: error }"
        :aria-invalid="!!error"
        :aria-describedby="error ? `${id}-error` : null"
        v-bind="$attrs"
    >
      <option v-if="placeholder" value="" disabled>{{ placeholder }}</option>
      <option
          v-for="option in options"
          :key="optionValueKey ? option[optionValueKey] : option"
          :value="optionValueKey ? option[optionValueKey] : option"
      >
        <template v-if="translationPrefix">
          {{ $t(`${translationPrefix}.${optionValueKey ? option[optionValueKey] : option}`) }}
        </template>
        <template v-else-if="optionLabelKey">
          {{ option[optionLabelKey] }}
        </template>
        <template v-else>
          {{ option }}
        </template>
      </option>
    </select>
    <p v-if="error" :id="`${id}-error`" class="error-message">{{ error }}</p>
  </div>
</template>

<style scoped>
.form-group {
  margin-bottom: 1.5rem;
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

select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  transition: all 0.2s;
  font-size: 1rem;
  background-color: var(--bg-color, white);
  color: var(--text-color, #333);
}

select:focus {
  border-color: #4f46e5;
  outline: none;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.2);
}

select.invalid {
  border-color: #e53e3e;
}

select.invalid:focus {
  box-shadow: 0 0 0 3px rgba(229, 62, 62, 0.2);
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