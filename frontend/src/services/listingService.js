// frontend/src/services/listingService.js
import apiClient from './client.js';

export const listingService = {
    createListing(listingData) {
        return apiClient.post('/listing', listingData);
    },
};