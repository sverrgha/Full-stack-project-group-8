// src/stores/auth.js
//Manages auth state, loading, errors, navigation
import { defineStore } from 'pinia';
import { authService } from '../services/authService.js';
import router from '../router';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: JSON.parse(localStorage.getItem('user')) || null,
        token: localStorage.getItem('token') || null,
        loading: false,
        error: null
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

        logout() {
            authService.logout();
            this.user = null;
            this.token = null;
            router.push('/login');
        },

        setAuthData(data) {
            this.user = {
                id: data.id,
                email: data.email
            };
            this.token = data.token;
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify(this.user));
            console.log("id: ", this.user.id)
        },

        clearError() {
            this.error = null;
        }
    }
});