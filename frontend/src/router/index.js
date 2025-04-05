// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../views/LoginPage.vue'
import RegisterPage from '../views/RegisterPage.vue'
import ProfilePage from '../views/ProfilePage.vue'
import ProfileSettingsPage from '../views/ProfileSettingsPage.vue'
import NewListingPage from '../views/NewListingPage.vue'
import MessagePage from "../views/MessagePage.vue";


const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginPage
    },
    {
        path: '/register',
        name: 'Register',
        component: RegisterPage
    },
    {
        path: '/profile',
        name: 'Profile',
        component: ProfilePage
    },
    {
        path: '/profile/settings',
        name: 'ProfileSettings',
        component: ProfileSettingsPage,
    },
    {
        path: '/new-listing',
        name: 'NewListing',
        component: NewListingPage,
    },
    {
        path: '/messages',
        name: 'messages',
        component: MessagePage,
    }

]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
