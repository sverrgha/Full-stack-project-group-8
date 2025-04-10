import apiClient from './client.js';

export const userService = {
    // Get user by ID
    getUserById(id) {
        return apiClient.get(`/user/${id}`);
    },

    // Update user details (name, email, etc.)
    updateUser(id, userData) {
        return apiClient.put(`/user/${id}`, userData);
    },

    // Update user password
    updatePassword(userId, passwords) {
        return apiClient.put(`/user/${userId}`, {
            password: passwords.newPassword
        });
    },
};