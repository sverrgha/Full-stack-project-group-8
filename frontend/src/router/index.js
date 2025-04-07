// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../views/LoginPage.vue'
import RegisterPage from '../views/RegisterPage.vue'
import ProfilePage from '../views/ProfilePage.vue'
import ProfileSettingsPage from '../views/ProfileSettingsPage.vue'
import ProductsPage from '../views/ProductsPage.vue'
import NewListingPage from '../views/NewListingPage.vue'
import MessagePage from "../views/MessagePage.vue"
import { useAuthStore } from "../stores/auth.js"
import ItemDetailPage from "../views/ItemDetailPage.vue"

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
        component: ProfilePage,
        meta: { requiresAuth: true }
    },
    {
        path: '/profile/settings',
        name: 'ProfileSettings',
        component: ProfileSettingsPage,
        meta: { requiresAuth: true }
    },
    {
        path: '/products',
        name: 'Products',
        component: ProductsPage,
        meta: { requiresAuth: false }
    },
    {
        path: '/new-listing',
        name: 'NewListing',
        component: NewListingPage,
        meta: { requiresAuth: true }
    },
    {
        path: '/messages',
        name: 'messages',
        component: MessagePage,
        meta: { requiresAuth: true }
    },
    {
        path: '/product/:id',
        name: 'product',
        component: ItemDetailPage
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const auth = useAuthStore()
    if (to.meta.requiresAuth && !auth.isAuthenticated) {
        next('/login')
    } else {
        next()
    }
})

export default router