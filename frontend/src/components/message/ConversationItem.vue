<script setup>
import { defineProps } from 'vue';

const props = defineProps({
  conversation: {
    type: Object,
    required: true
  },
  isSelected: Boolean,
  onSelect: Function
});
</script>

<template>
  <div
      class="conversation-item"
      :class="{ 'selected': isSelected, 'unread': conversation.unread }"
      @click="onSelect"
  >
    <div class="avatar">
      <div class="avatar-placeholder">{{ conversation.email.charAt(0) }}</div>
    </div>
    <div class="conversation-info">
      <div class="conversation-header">
        <span class="username">{{ conversation.email }}</span>
        <span class="timestamp">{{ conversation.timestamp }}</span>
      </div>
      <div class="last-message">{{ conversation.lastMessage }}</div>
    </div>
  </div>
</template>

<style scoped>
.conversation-item {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.conversation-item:hover {
  background-color: #f9f9f9;
}

.conversation-item.selected {
  background-color: #f0f7ff;
}

.conversation-item.unread .username {
  font-weight: bold;
}

.avatar {
  width: 40px;
  height: 40px;
  margin-right: 15px;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background-color: #007bff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.conversation-info {
  flex: 1;
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.timestamp {
  font-size: 0.8rem;
  color: #888;
}

.last-message {
  font-size: 0.9rem;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>