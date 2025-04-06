<script setup>
import { ref, computed } from 'vue';
import InputField from '../components/form/BaseInputField.vue';

// Mock data for conversations (would come from API in the future)
// Update the conversations data to include profile images
const conversations = ref([
  { id: 1, username: 'John Doe', lastMessage: 'Hi there!', timestamp: '10:30', unread: true, profileImage: 'https://i.pravatar.cc/150?img=1' },
  { id: 2, username: 'Jane Smith', lastMessage: 'When will the item be available?', timestamp: 'Yesterday', unread: false, profileImage: 'https://i.pravatar.cc/150?img=5' },
  { id: 3, username: 'Mike Johnson', lastMessage: 'Thanks for the info', timestamp: 'Monday', unread: false, profileImage: 'https://i.pravatar.cc/150?img=3' },
  { id: 4, username: 'Sara Wilson', lastMessage: 'Is the price negotiable?', timestamp: 'Last week', unread: true}
]);

// Mock data for messages in a conversation (would come from API in the future)
const messagesByConversation = {
  1: [
    { id: 1, sender: 'John Doe', content: 'Hi there!', timestamp: '10:30', isSent: false },
    { id: 2, sender: 'Me', content: 'Hello! How can I help you?', timestamp: '10:32', isSent: true },
  ],
  2: [
    { id: 3, sender: 'Jane Smith', content: 'Is the item still available?', timestamp: 'Yesterday 15:45', isSent: false },
    { id: 4, sender: 'Me', content: 'Yes, it is!', timestamp: 'Yesterday 16:00', isSent: true },
    { id: 5, sender: 'Jane Smith', content: 'When will the item be available?', timestamp: 'Yesterday 16:05', isSent: false },
  ],
  4: [
    { id: 6, sender: 'Sara Wilson', content: 'Is the price negotiable?', timestamp: 'Last week', isSent: false },
    { id: 7, sender: 'Me', content: 'Yes, what did you have in mind?', timestamp: 'Last week', isSent: true },
    {
      id: 8,
      sender: 'Sara Wilson',
      content: '$75',
      timestamp: 'Last week',
      isSent: false,
      type: 'offer',
      originalPrice: '$100',
      offerPrice: '$75',
      status: 'pending'
    },
  ],
};

const selectedConversation = ref(4);
const newMessage = ref('');
const showSideMenu = ref(false);

const currentMessages = computed(() => {
  return messagesByConversation[selectedConversation.value] || [];
});

const selectConversation = (id) => {
  selectedConversation.value = id;
  showSideMenu.value = false; // Hide side menu on mobile when a conversation is selected
  // In the future: Mark conversation as read when selected
};

const sendMessage = () => {
  if (!newMessage.value.trim()) return;

  // In a real app, this would send to an API
  const message = {
    id: Math.random().toString(36).substring(2, 9),
    sender: 'Me',
    content: newMessage.value,
    timestamp: new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}),
    isSent: true
  };

  if (!messagesByConversation[selectedConversation.value]) {
    messagesByConversation[selectedConversation.value] = [];
  }

  messagesByConversation[selectedConversation.value].push(message);
  newMessage.value = '';
};

// Send offer from the current user (this would normally be triggered from listing page)
const sendOffer = (price) => {
  const offer = {
    id: Math.random().toString(36).substring(2, 9),
    sender: 'Me',
    content: price,
    timestamp: new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}),
    isSent: true,
    type: 'offer',
    originalPrice: '$100',
    offerPrice: price,
    status: 'pending'
  };

  if (!messagesByConversation[selectedConversation.value]) {
    messagesByConversation[selectedConversation.value] = [];
  }

  messagesByConversation[selectedConversation.value].push(offer);
};

const respondToOffer = (messageId, response) => {
  // Find and update the offer message
  const messages = messagesByConversation[selectedConversation.value];
  const offerIndex = messages.findIndex(msg => msg.id === messageId);

  if (offerIndex >= 0) {
    messages[offerIndex].status = response;
  }

  // Add a response message
  const responseText = response === 'accepted'
      ? `I accept your offer of ${messages[offerIndex].offerPrice}.`
      : `Sorry, I cannot accept your offer of ${messages[offerIndex].offerPrice}.`;

  const message = {
    id: Math.random().toString(36).substring(2, 9),
    sender: 'Me',
    content: responseText,
    timestamp: new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}),
    isSent: true
  };

  messages.push(message);
};
</script>

<template>
  <div class="message-container">
    <!-- Left sidebar - Conversation list -->
    <div class="conversation-list" :class="{ 'show': showSideMenu }">
      <div class="header">
        <h2>Messages</h2>
      </div>

      <div
          v-for="conv in conversations"
          :key="conv.id"
          class="conversation-item"
          :class="{ 'selected': selectedConversation === conv.id, 'unread': conv.unread }"
          @click="selectConversation(conv.id)"
      >
        <div class="avatar">
          <img
              v-if="conv.profileImage"
              :src="conv.profileImage"
              alt="Profile"
              class="avatar-image"
          />
          <template v-else>{{ conv.username.charAt(0) }}</template>
        </div>
        <div class="conversation-info">
          <div class="conversation-header">
            <span class="username">{{ conv.username }}</span>
            <span class="timestamp">{{ conv.timestamp }}</span>
          </div>
          <div class="last-message">{{ conv.lastMessage }}</div>
        </div>
      </div>

    </div>
    <!-- Right side - Message content -->
    <div class="message-content">
      <div class="header">
        <button class="toggle-menu" @click="showSideMenu = !showSideMenu">
          <span class="toggle-icon">&lt;</span>
        </button>
        <div class="user-header">
          <div class="user-avatar">
            <img
                v-if="conversations.find(c => c.id === selectedConversation)?.profileImage"
                :src="conversations.find(c => c.id === selectedConversation)?.profileImage"
                alt="Profile"
            />
            <div v-else class="avatar-placeholder">
              {{ conversations.find(c => c.id === selectedConversation)?.username.charAt(0) }}
            </div>
          </div>
          <h3>{{ conversations.find(c => c.id === selectedConversation)?.username || 'Select a conversation' }}</h3>
        </div>
      </div>

      <div class="messages-container">
        <div
            v-for="msg in currentMessages"
            :key="msg.id"
            class="message"
            :class="{ 'sent': msg.isSent, 'received': !msg.isSent }"
        >
          <!-- Regular message -->
          <div v-if="!msg.type" class="message-bubble">
            {{ msg.content }}
          </div>

          <!-- Offer message -->
          <div v-else-if="msg.type === 'offer'" class="message-bubble offer">
            <div class="offer-text">
              <strong>Price Offer</strong>
              <div>Original: {{ msg.originalPrice }}</div>
              <div>Offer: {{ msg.offerPrice }}</div>
              <div v-if="msg.status === 'accepted'" class="offer-status accepted">Accepted</div>
              <div v-else-if="msg.status === 'declined'" class="offer-status declined">Declined</div>
              <div v-else class="offer-status pending">Pending</div>
            </div>
            <!-- Only show buttons if the offer was received and is still pending -->
            <div v-if="!msg.isSent && msg.status === 'pending'" class="offer-actions">
              <button @click="respondToOffer(msg.id, 'accepted')" class="accept-btn">Accept</button>
              <button @click="respondToOffer(msg.id, 'declined')" class="decline-btn">Decline</button>
            </div>
          </div>

          <div class="message-timestamp">{{ msg.timestamp }}</div>
        </div>
      </div>

      <div class="message-input">
        <input
            type="text"
            v-model="newMessage"
            placeholder="Type a message..."
            @keyup.enter="sendMessage"
        />
        <button @click="sendMessage">Send</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.message-container {
  display: flex;
  height: calc(100vh - 60px);
  background-color: #f5f5f5;
}

.toggle-menu {
  display: none;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  margin-right: 10px;
}

.conversation-list {
  width: 30%;
  min-width: 250px;
  border-right: 1px solid #ddd;
  background-color: white;
  overflow-y: auto;
  transition: transform 0.3s ease;
}

.conversation-list.show {
  transform: translateX(0);
}

.message-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #ddd;
  background-color: white;
}

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
  border-radius: 50%;
  background-color: #007bff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-weight: bold;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
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

.messages-container {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.message {
  max-width: 70%;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
}

.message.received {
  align-self: flex-start;
}

.message.sent {
  align-self: flex-end;
}

.message-bubble {
  padding: 10px 15px;
  border-radius: 18px;
  background-color: #f1f1f1;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

.message.sent .message-bubble {
  background-color: #007bff;
  color: white;
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
  margin-left: 5px;
  margin-right: 5px;
}

.message-input {
  display: flex;
  padding: 15px;
  background-color: white;
  border-top: 1px solid #ddd;
}

.message-input input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 20px;
  margin-right: 10px;
}

.message-input button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
}

.user-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #007bff;
  color: white;
  font-weight: bold;
  font-size: 1.2rem;
}

/* Responsive design */
@media (max-width: 768px) {
  .message-container {
    flex-direction: column;
  }

  .toggle-menu {
    display: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    margin-right: 15px;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: #f0f7ff none;
    align-items: center;
    justify-content: center;
    padding: 0;
    transition: background-color 0.2s ease;
  }

  .toggle-menu:hover {
    background-color: #e1efff;
  }

  .toggle-icon {
    display: inline-flex;
    font-weight: bold;
    color: #007bff;
    font-size: 1.2rem;
    margin-right: 2px;
  }
  .toggle-menu {
    display: block;
  }

  .conversation-list {
    position: absolute;
    top: 0;
    left: 0;
    width: 80%;
    min-width: unset;
    height: 100%;
    transform: translateX(-100%);
    z-index: 1000;
  }

  .conversation-list.show {
    transform: translateX(0);
  }

  .message-content {
    flex: 1;
  }

  .message-input {
    flex-wrap: wrap;
  }

  .message-input button {
    margin-top: 10px;
  }
}
</style>