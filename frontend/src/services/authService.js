//Encapsulates login/register/logout HTTP logic
import apiClient from './client.js'
import axios from 'axios';
import { useAuthStore } from '../stores/auth';

// Set up axios interceptor outside the service object
axios.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401) {
            // Token expired
            const authStore = useAuthStore();
            authStore.logout();
            // Optional: show notification to user
        }
        return Promise.reject(error);
    }
);

export const authService = {
    register(userData) {
        return apiClient.post('/auth/register', userData)
    },

    login(credentials) {
        return apiClient.post('/auth/login', credentials)
    },

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    }
};