<script setup>
import {ref, computed, onMounted} from 'vue';
import { useMessageStore } from "../stores/messages.js";
import ConversationItem from "../components/message/ConversationItem.vue";
import MessageBubble from "../components/message/MessageBubble.vue";
import {useAuthStore} from "../stores/auth.js";

const messageStore = useMessageStore();
const selectedConversation = ref(null);
const newMessage = ref('');
const showSideMenu = ref(false);

const authStore = useAuthStore();
const currentUserId = computed(() => authStore.user?.id);

// Load inbox on component mount
onMounted(async () => {
  console.log("Component mounted, fetching inbox");
  try {
    await messageStore.fetchInbox();
    console.log("Inbox fetched:", messageStore.inbox);
  } catch (error) {
    console.error("Error in onMounted:", error);
  }
})

// Computed properties for conversations and messages
const conversations = computed(() => messageStore.inbox);
const currentMessages = computed(() =>
    selectedConversation.value ? messageStore.currentConversation : []
);

const selectConversation = async (userId) => {
  console.log("Selecting conversation with user ID:", userId);
  selectedConversation.value = userId;
  showSideMenu.value = false;

  try {
    await messageStore.fetchConversation(userId);
    console.log("Conversation fetched:", messageStore.currentConversation);
  } catch (error) {
    console.error('Failed to fetch conversation:', error);
  }
};

const sendMessage = async () => {
  if (!newMessage.value.trim() || !selectedConversation.value) return;

  try {
    const currentUserEmail = authStore.user.email; // Get current user's email
    await messageStore.sendMessage(
        currentUserEmail,
        selectedConversation.value, // This should already be the receiver's email
        newMessage.value
    );
    newMessage.value = '';
  } catch (error) {
    console.error('Failed to send message:', error);
  }
};

const sendOffer = async (price) => {
  if (!selectedConversation.value) return;

  try {
    await messageStore.sendMessage(
        currentUserId.value,
        selectedConversation.value,
        JSON.stringify({
          type: 'offer',
          originalPrice: '$100', // Replace with actual listing price
          offerPrice: price,
          status: 'pending'
        })
    );
  } catch (error) {
    console.error('Failed to send offer:', error);
  }
};

const respondToOffer = async (messageId, response) => {
  if (!selectedConversation.value) return;

  try {
    // Send response message
    await messageStore.sendMessage(
        currentUserId.value,
        selectedConversation.value,
        response === 'accepted'
            ? `Offer accepted: ${messageId}`
            : `Offer declined: ${messageId}`
    );
  } catch (error) {
    console.error('Failed to respond to offer:', error);
  }
};
</script>

<template>
  <div class="message-container">
    <!-- Left sidebar - Conversation list -->
    <div class="conversation-list" :class="{ 'show': showSideMenu }">
      <div class="header">
        <h2>Messages</h2>
      </div>

      <div v-if="messageStore.loading">Loading conversations...</div>
      <div v-else-if="conversations.length === 0">No conversations yet</div>
      <div v-else>
        <ConversationItem
            v-for="conv in conversations"
            :key="conv.id"
            :conversation="conv"
            :isSelected="selectedConversation === conv.id"
            :onSelect="() => selectConversation(conv.email)"
        />
      </div>
    </div>

    <!-- Right side - Message content -->
    <div class="message-content">
      <div class="header">
        <button class="toggle-menu" @click="showSideMenu = !showSideMenu">
          <span class="toggle-icon">&lt;</span>
        </button>
        <div class="user-header" v-if="selectedConversation">
          <div class="avatar-placeholder">
            {{ conversations.find(c => c.id === selectedConversation)?.email.charAt(0) }}
          </div>
          <h3>{{ conversations.find(c => c.id === selectedConversation)?.email || 'Select a conversation' }}</h3>
        </div>
        <div v-else class="user-header">
          <h3>Select a conversation</h3>
        </div>
      </div>

      <div class="messages-container">
        <div v-if="messageStore.loading">Loading messages...</div>
        <div v-else-if="!selectedConversation">Select a conversation to view messages</div>
        <div v-else-if="currentMessages.length === 0">No messages in this conversation yet</div>
        <template v-else>
          <MessageBubble
              v-for="msg in currentMessages"
              :key="msg.id"
              :message="msg"
              :onRespondToOffer="respondToOffer"
          />
        </template>
      </div>

      <div class="message-input" v-if="selectedConversation">
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

.messages-container {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
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