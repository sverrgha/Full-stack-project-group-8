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

                this.listings = response.data.elements;

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

        async updateListing(id, listingData) {
            try {
                const response = await listingService.updateListing(id, listingData);
                // Update the current listing in the store
                await this.fetchListingById(id);
                return response;
            } catch (error) {
                console.error("Error updating listing:", error);
                throw error;
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

        async deleteListing(id) {
            this.loading = true;
            this.error = null;

            try {
                const response = await listingService.deleteListing(id);
                // Optionally, you can refresh the listings after deletion
                await this.fetchListings();
                return response.data;
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to delete listing';
                throw error;
            } finally {
                this.loading = false;
            }
        },

        async updateListingStatus(id, status) {
            this.loading = true;
            this.error = null;

            try {
                const response = await listingService
                    .updateListingStatus(id, status);
                return response.data;
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to update listing status';
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

        clearCurrentListing() {
            this.currentListing = null;
        }
    }
})