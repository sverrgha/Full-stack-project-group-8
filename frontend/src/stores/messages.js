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

        async sendMessage(sender, receiver, content) {
            try {
                this.loading = true;
                const response = await messageService.send(sender, receiver, content);
                this.currentConversation.push({
                    id: response.id,
                    sender: sender,
                    receiver: receiver,
                    content: content,
                    timestamp: response.timestamp,
                    isSent: true
                });
                return response;
            } catch (error) {
                this.error = error.message;
                throw error;
            } finally {
                this.loading = false;
            }
        },

        async fetchConversation(endUserId) {
            try {
                this.loading = true;
                const authStore = useAuthStore();
                const currentUserId = authStore.user?.id;

                const response = await messageService.getConversation(endUserId);
                this.currentConversation = response.map(msg => ({
                    id: msg.id,
                    sender: msg.sender,
                    receiver: msg.receiver,
                    content: msg.content,
                    timestamp: msg.timestamp,
                    isSent: msg.sender === currentUserId
                }));
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