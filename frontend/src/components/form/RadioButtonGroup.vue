<!-- RadioButtonGroup.vue -->
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
    required: true
  },
  options: {
    type: Array,
    required: true
  },
  translationPrefix: {
    type: String,
    default: null
  }
});

const emit = defineEmits(['update:modelValue']);

const handleOptionClick = (value) => {
  emit('update:modelValue', value);
};

const handleKeyDown = (event, value) => {
  if (event.key === 'Enter' || event.key === ' ') {
    event.preventDefault();
    emit('update:modelValue', value);
  }
};
</script>

<template>
  <div class="form-group">
    <label :id="`${id}-label`">{{ label }}</label>
    <div
        class="option-buttons"
        role="radiogroup"
        :aria-labelledby="`${id}-label`"
    >
      <div
          v-for="option in options"
          :key="option"
          class="option-button"
          :class="{ selected: modelValue === option }"
          @click="handleOptionClick(option)"
          @keydown="handleKeyDown($event, option)"
          role="radio"
          :aria-checked="modelValue === option"
          tabindex="0"
      >
        <template v-if="translationPrefix">
          {{ $t(`${translationPrefix}.${option}`) }}
        </template>
        <template v-else>
          {{ option }}
        </template>
      </div>
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

.option-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option-button {
  padding: 10px 16px;
  border: 1px solid #ccc;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}

.option-button:hover {
  border-color: #4f46e5;
  background-color: rgba(79, 70, 229, 0.05);
}

.option-button:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.2);
}

.option-button.selected {
  background-color: #4f46e5;
  color: white;
  border-color: #4f46e5;
}

@media (max-width: 676px) {
  .option-buttons {
    flex-direction: column;
  }
}
</style>