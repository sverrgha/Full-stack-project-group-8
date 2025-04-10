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

    async send(senderEmail, receiverEmail, content) {
        try {
            const response = await apiClient.post('/messages/send', {
                sender: senderEmail,
                receiver: receiverEmail,
                content: content
            });
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    async getConversation(endUserEmail) {
        try {
            const response = await apiClient.get(`/messages/conversation/${endUserEmail}`);
            return response.data;
        } catch (error) {
            console.error("Error in getConversation:", error);
            throw error;
        }
    }
}