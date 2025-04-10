import apiClient from './client.js';

export const listingService = {
    //Get listings with optinal filters and pagination
    getListings(filterParams = {}, pageable = {}) {
        return apiClient.get('/listing', {
            params: {
                ...filterParams,
                ...pageable
            }
        });
    },

    //Get a single listing by ID
    getListingById(id) {
        return apiClient.get(`/listing/${id}`);
    },

    // Add a new listing
    addListing(listingData) {
        return apiClient.post('/listing', listingData);
    },

    // Update the status of a listing
    updateListingStatus(id, status) {
        return apiClient.put(`/listing/${id}/status`, { status });
    },

    //Get recommended listing for a user
    getRecommendedListings(userId, pageable = {}) {
        return apiClient.get('/listing/user/recommended', {
            params: {
                userId,
                ...pageable
            }
        });
    }
};