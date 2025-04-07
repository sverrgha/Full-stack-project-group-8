//Encapsulates login/register/logout HTTP logic
import apiClient from './client.js'

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
}