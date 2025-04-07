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

    //Get a sinle listing by ID
    getListingById(id) {
        return apiClient.get(`/listing/${id}`);
    },

    // Add a new listing
    addListing(listingData) {
        return apiClient.post('/listing', listingData);
    },

    //Get recommended listing for a user
    getRecommendedListings(userId, pageable = {}) {
        return apiClient.get('/listing/user/recomended', {
            params: {
                userId,
                ...pageable
            }
        });
    }
};