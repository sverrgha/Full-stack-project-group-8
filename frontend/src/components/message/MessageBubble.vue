<script setup>
import { defineProps } from 'vue';

const props = defineProps({
  message: Object,
  onRespondToOffer: Function
});

// For debugging
console.log('Message:', props.message);
</script>

<template>
  <div :class="{ 'sent': message.isSent, 'received': !message.isSent }" class="message">
    <!-- Regular message -->
    <div v-if="!message.type" class="message-bubble">
      {{ message.content }}
    </div>

    <!-- Offer message -->
    <div v-else-if="message.type === 'offer'" class="message-bubble offer">
      <div class="offer-text">
        <strong>Price Offer</strong>
        <div>Original: {{ message.originalPrice }}</div>
        <div>Offer: {{ message.offerPrice }}</div>
        <div v-if="message.status === 'accepted'" class="offer-status accepted">Accepted</div>
        <div v-else-if="message.status === 'declined'" class="offer-status declined">Declined</div>
        <div v-else class="offer-status pending">Pending</div>
      </div>
      <!-- Only show buttons if the offer was received and is still pending -->
      <div v-if="!message.isSent && message.status === 'pending'" class="offer-actions">
        <button @click="onRespondToOffer(message.id, 'accepted')" class="accept-btn">Accept</button>
        <button @click="onRespondToOffer(message.id, 'declined')" class="decline-btn">Decline</button>
      </div>
    </div>

    <div class="message-timestamp">{{ message.timestamp }}</div>
  </div>
</template>

<style scoped>
.message {
  max-width: 70%;
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
  clear: both;
  width: fit-content;
}

.message.received {
  align-self: flex-start;
  margin-right: auto;
}

.message.sent {
  align-self: flex-end;
  margin-left: auto;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 18px;
  background-color: #f1f1f1;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  word-break: break-word;
}

.message.sent .message-bubble {
  background-color: #0084ff;
  color: white;
  border-top-right-radius: 4px;
}

.message.received .message-bubble {
  background-color: #e9e9eb;
  color: #000;
  border-top-left-radius: 4px;
}

.message-bubble.offer {
  background-color: #f8f9fa;
  border: 1px solid #ddd;
  padding: 12px;
}

.message.sent .message-bubble.offer {
  background-color: #e6f2ff;
  color: #333;
  border-color: #b8daff;
}

.offer-text {
  margin-bottom: 8px;
}

.offer-status {
  font-size: 0.8rem;
  font-weight: bold;
  margin-top: 5px;
}

.offer-status.pending {
  color: #ffc107;
}

.offer-status.accepted {
  color: #28a745;
}

.offer-status.declined {
  color: #dc3545;
}

.offer-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.accept-btn {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.decline-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.message-timestamp {
  font-size: 0.7rem;
  color: #888;
  margin-top: 3px;
  align-self: flex-end;
}

.message.received .message-timestamp {
  align-self: flex-start;
}
</style>