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
  },
  hideLabel: {
    type: Boolean,
    default: false
  },
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  helperText: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue', 'change', 'input', 'focus', 'blur']);

const handleInput = (event) => {
  emit('update:modelValue', event.target.value);
  emit('input', event);
};

const handleChange = (event) => {
  emit('update:modelValue', event.target.value);
  emit('change', event);
};

const handleFocus = (event) => {
  emit('focus', event);
};

const handleBlur = (event) => {
  emit('blur', event);
};

const sizeClasses = computed(() => {
  switch (props.size) {
    case 'small': return 'text-sm py-1 px-2';
    case 'large': return 'text-lg py-3 px-4';
    default: return 'text-base py-2 px-3';
  }
});

const selectedOptionLabel = computed(() => {
  if (!props.modelValue) return '';

  const selectedOption = props.options.find(option => {
    const optionValue = props.optionValueKey ? option[props.optionValueKey] : option;
    return optionValue === props.modelValue;
  });

  if (!selectedOption) return '';

  if (props.translationPrefix) {
    // This would need to be handled with $t in the template
    return `${props.translationPrefix}.${props.optionValueKey ? selectedOption[props.optionValueKey] : selectedOption}`;
  } else if (props.optionLabelKey) {
    return selectedOption[props.optionLabelKey];
  } else {
    return selectedOption;
  }
});
</script>

<template>
  <div class="form-group">
    <label :for="id" :class="{ 'sr-only': hideLabel }">
      {{ label }} <span v-if="required" class="required">*</span>
    </label>

    <div class="select-wrapper">
      <select
          :id="id"
          :value="modelValue"
          @input="handleInput"
          @change="handleChange"
          @focus="handleFocus"
          @blur="handleBlur"
          :class="[
            sizeClasses,
            {
              'invalid': error,
              'has-placeholder': !modelValue && placeholder
            }
          ]"
          :aria-invalid="!!error"
          :aria-describedby="(error || helperText) ? `${id}-description` : null"
          :disabled="disabled"
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

      <!-- Custom arrow icon slot -->
      <slot name="arrow-icon">
        <span class="select-arrow"></span>
      </slot>
    </div>

    <!-- Helper text area (includes error message) -->
    <div
        v-if="error || helperText"
        :id="`${id}-description`"
        class="description-text"
    >
      <p v-if="error" class="error-message">{{ error }}</p>
      <p v-else-if="helperText" class="helper-text">{{ helperText }}</p>
    </div>
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

.select-wrapper {
  position: relative;
  display: block;
}

select {
  width: 100%;
  padding: 0.75rem;
  padding-right: 2rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  transition: all 0.2s;
  font-size: 1rem;
  background-color: var(--bg-color, white);
  color: var(--text-color, #333);
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  cursor: pointer;
}

select::-ms-expand {
  display: none;
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

select:disabled {
  background-color: #f9f9f9;
  cursor: not-allowed;
  opacity: 0.7;
}

select.has-placeholder {
  color: #777;
}

.select-arrow {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 5px solid #666;
  pointer-events: none;
}

.description-text {
  margin-top: 0.25rem;
  font-size: 0.875rem;
}

.error-message {
  color: #e53e3e;
  animation: fadeIn 0.2s;
}

.helper-text {
  color: #666;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>