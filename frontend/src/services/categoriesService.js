// src/services/categoriesService.js
import apiClient from './client.js';

export const categoriesService = {
    getAllCategories() {
        return apiClient.get('/category');
    },

    createCategory(categoryData) {
        return apiClient.post('/category', categoryData);
    },
};