import apiClient from './client.js'
import axios from 'axios';
import { useAuthStore } from '../stores/auth';

export const messageService = {
    async getInbox () {
        try {
            const response = await apiClient.get('/messages/inbox')
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    async send(sender, receiver, content) {
        try {
            const messageData = {
                sender: sender,
                receiver: receiver,
                content: content
            };
            const response = await apiClient.post('/messages/send', messageData)
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    async getConversation(endUserId) {
        try {
            const response = await apiClient.get(`/messages/conversation/${endUserId}`, {
                params: { endUserId }
            });
            return response.data;
        } catch (error) {
            console.error("Error in getConversation:", error);
            throw error;
        }
    }
}