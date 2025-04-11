import {defineStore} from "pinia";
import { listingService} from "../services/listingService.js";

export const useListingStore = defineStore('listing', {
    state: () => ({
        listings: [],
        currentListing: null,
        loading: false,
        error: null,
        totalElements: 0,
        totalPages: 0,
        currentPage: 1,
        pageSize: 20,
        hasNext: false,
        hasPrevious: false
    }),

    getters: {
        getListings: (state) => state.listings,
        getCurrentListing: (state) => state.currentListing
    },

    actions: {
        async fetchListings(filters = {}, page = 1, size = 20) {
            this.loading = true;
            this.error = null;

            try {
                const pageable = {
                    page,
                    size
                };

                const response = await listingService.getListings(filters, pageable);

                if (page === 1) {
                    this.listings = response.data.elements;
                } else {
                    this.listings = [...this.listings, ...response.data.elements];
                }

                this.totalElements = response.data.totalElements;
                this.totalPages = response.data.totalPages;
                this.currentPage = response.data.currentPage;
                this.pageSize = response.data.pageSize;
                this.hasNext = response.data.hasNext;
                this.hasPrevious = response.data.hasPrevious;
                return response.data;
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to fetch listings';
                throw error;
            } finally {
                this.loading = false;
            }
        },

        async fetchListingById(id) {
            this.loading = true;
            this.error = null;

            try {
                const response = await listingService.getListingById(id);
                this.currentListing = response.data;
                return response.data;
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to fetch listing';
                throw error;
            } finally {
                this.loading = false;
            }
        },

        async addListing(listingData) {
            this.loading = true;
            this.error = null;

            try {
                const response = await listingService.addListing(listingData);
                return response.data;
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to add listing';
                throw error;
            } finally {
                this.loading = false;
            }
        },

        async fetchRecommendedListings(userId, page = 1, size = 20) {
            this.loading = true;
            this.error = null;

            try {
                const pageable = { page, size };
                const response = await listingService.getRecommendedListings(userId, pageable);
                return response.data;
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to fetch recommended listings';
                throw error;
            } finally {
                this.loading = false;
            }
        },

        async fetchPersonalListings(userId, page = 1, size = 20) {
            this.loading = true;
            this.error = null;

            try {
                const pageable = { page, size };
                const response = await listingService.getPersonalListings(userId, pageable);
                return this.handleMultipleListings(response, page.value);
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to fetch personal listings';
                throw error;
            } finally {
                this.loading = false;
            }
        },

        async fetchFavoriteListings(userId, page = 1, size = 20) {
            this.loading = true;
            this.error = null;

            try {
                const pageable = { page, size };
                const response = await listingService.getFavoriteListings(userId, pageable);
                return this.handleMultipleListings(response, page.value);
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to fetch favorite listings';
                throw error;
            } finally {
                this.loading = false;
            }
        },

        clearCurrentListing() {
            this.currentListing = null;
        },

        handleMultipleListings(response, page) {
            if (page === 1) {
                this.listings = response.data.elements;
            } else {
                this.listings = [...this.listings, ...response.data.elements];
            }
            this.totalElements = response.data.totalElements;
            this.totalPages = response.data.totalPages;
            this.currentPage = response.data.currentPage;
            this.pageSize = response.data.pageSize;
            this.hasNext = response.data.hasNext;
            this.hasPrevious = response.data.hasPrevious;
            return response.data;
        },

        async addFavorite(userId, listingId) {
            this.loading = true;
            this.error = null;

            try {
                listingService.addFavorite(userId, listingId);
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to add favorite';
                throw error;
            } finally {
                this.loading = false;
            }
        },

        async removeFavorite(userId, listingId) {
            this.loading = true;
            this.error = null;

            try {
                await listingService.removeFavorite(userId, listingId);
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to remove favorite';
                throw error;
            } finally {
                this.loading = false;
            }
        }
    }
})