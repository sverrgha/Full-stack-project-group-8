// src/stores/auth.js
//Manages auth state, loading, errors, navigation
import { defineStore } from 'pinia';
import { authService } from '../services/authService.js';
import router from '../router';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: JSON.parse(localStorage.getItem('user')) || null,
        token: localStorage.getItem('token') || null,
        tokenExpiration: localStorage.getItem('tokenExpiration') || null,
        loading: false,
        error: null,
        logoutTimer: null
    }),

    getters: {
        isAuthenticated: (state) => !!state.token,
        getUser: (state) => state.user,
        getCurrentUserId: (state) => state.user?.id
    },

    actions: {
        async register(userData) {
            this.loading = true;
            this.error = null;

            try {
                const response = await authService.register(userData);
                this.setAuthData(response.data);
                await router.push('/products');
                return response;
            } catch (error) {
                this.error = error.response?.data?.message || 'Registration failed';
                throw error;
            } finally {
                this.loading = false;
            }
        },

        async login(credentials) {
            this.loading = true;
            this.error = null;

            try {
                const response = await authService.login(credentials);
                this.setAuthData(response.data);
                await router.push('/products');
                return response;
            } catch (error) {
                this.error = error.response?.data?.message || 'Login failed';
                throw error;
            } finally {
                this.loading = false;
            }
        },

        async refreshToken() {
            try {
                const response = await authService.refreshToken();
                this.setAuthData(response.data);
                return response;
            } catch (error) {
                this.logout();
                throw error;
            }
        },

        logout() {
            // Clear the logout timer
            if (this.logoutTimer) {
                clearTimeout(this.logoutTimer);
                this.logoutTimer = null;
            }

            authService.logout();
            this.user = null;
            this.token = null;
            this.tokenExpiration = null;
            localStorage.removeItem('tokenExpiration');
            router.push('/login');
        },

        setAuthData(data) {
            this.user = {
                id: data.id,
                email: data.email
            };
            this.token = data.token;
            this.tokenExpiration = data.expirationDate;

            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify(this.user));
            localStorage.setItem('tokenExpiration', data.expirationDate);

            //set auto-logout timer
            this.setLogoutTimer();
        },

        setLogoutTimer() {
            if (this.logoutTimer) {
                clearTimeout(this.logoutTimer);
            }

            if (!this.tokenExpiration) return;

            const expirationDate = new Date(this.tokenExpiration).getTime();
            const now = new Date().getTime();
            const timeUntilExpiration = expirationDate - now;

            // Logout if token already expired
            if (timeUntilExpiration <= 0) {
                this.logout();
                return;
            }

            // Calculate refresh time (80% of total time)
            const refreshTime = timeUntilExpiration * 0.8;

            // Set timer to refresh token at 80% of expiration time
            this.logoutTimer = setTimeout(async () => {
                try {
                    await this.refreshToken();
                } catch (error) {
                    // If refresh fails, logout when token expires
                    const remainingTime = timeUntilExpiration - refreshTime;
                    this.logoutTimer = setTimeout(() => {
                        this.logout();
                    }, remainingTime);
                }
            }, refreshTime);
        },

        // Add an initialization method to check token expiration on app start
        initAuth() {
            if (this.token && this.tokenExpiration) {
                this.setLogoutTimer();
            }
        },

        clearError() {
            this.error = null;
        }
    }
});