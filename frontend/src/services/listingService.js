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
    },

    getPersonalListings(userId, pageable = {}) {
        return apiClient.get(`/listing/${userId}/posted`, {
            params: {
                size: pageable.size?.value,
                page: pageable.page?.value,
            }
        });
    },

    getFavoriteListings(userId, pageable = {}) {
        console.log(pageable);
        return apiClient.get(`/favorites/${userId}`, {
            params: {
                size: pageable.size?.value,
                page: pageable.page?.value,
            }
        });
    },

    addFavorite(userId, listingId) {
        return apiClient.post('/favorites', {
                userId: userId,
                listingId: listingId
            }
        );
    },

    removeFavorite(userId, listingId) {
        return apiClient.delete(`/favorites`, {
                data: {
                    userId: userId,
                    listingId: listingId
                }
            }
        )
            ;
    },
};