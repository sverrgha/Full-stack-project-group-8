import { defineStore } from "pinia";
import { messageService } from "../services/messageService.js";
import { useAuthStore} from "./auth.js";

export const useMessageStore = defineStore('messages', {
    state: () => ({
        inbox: [],
        currentConversation: [],
        error: null,
        loading: false
    }),

    actions: {
        async fetchInbox() {
            try {
                this.loading = true;
                const authStore = useAuthStore();
                const currentUserId = authStore.user?.id;

                const response = await messageService.getInbox();
                this.inbox = response.map(conv => ({
                    id: conv.senderId === currentUserId ? conv.receiverId : conv.senderId,
                    email: conv.otherUserEmail,
                    lastMessage: conv.lastMessage,
                    timestamp: conv.timestamp,
                    unread: !conv.read
                }));
            } catch (error) {
                this.error = error.message;
                console.error("Error fetching inbox:", error);
            } finally {
                this.loading = false;
            }
        },

        async sendMessage(senderEmail, receiverEmail, content) {
            try {
                const response = await messageService.send(senderEmail, receiverEmail, content);
                await this.fetchConversation(receiverEmail);
                return response;
            } catch (error) {
                console.error('Failed to send message:', error);
                throw error;
            }
        },

        async fetchConversation(endUserId) {
            try {
                this.loading = true;
                const authStore = useAuthStore();
                const currentUserId = authStore.user?.id;

                const response = await messageService.getConversation(endUserId);
                this.currentConversation = response.map(msg => {
                    // Try to parse JSON content for special message types
                    let parsedContent = null;
                    let type = null;

                    try {
                        parsedContent = JSON.parse(msg.content);
                        if (parsedContent && typeof parsedContent === 'object' && parsedContent.type) {
                            type = parsedContent.type;
                        }
                    } catch (e) {
                        // Not JSON content, treat as regular message
                    }

                    return {
                        id: msg.id,
                        sender: msg.sender,
                        receiver: msg.receiver,
                        content: type ? parsedContent.content : msg.content,
                        timestamp: msg.timestamp,
                        isSent: msg.sender === currentUserId,
                        type: type,
                        originalPrice: parsedContent?.originalPrice,
                        offerPrice: parsedContent?.offerPrice,
                        status: parsedContent?.status
                    };
                });
            } catch (error) {
                this.error = error.message;
                console.error("Error fetching conversation:", error);
                throw error;
            } finally {
                this.loading = false;
            }
        }
    }
});