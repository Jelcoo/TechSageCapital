import { useUserStore } from '@/stores/user';
import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import('@/views/HomeView.vue'),
        },
        {
            path: '/accountdetails',
            name: 'accountdetails',
            meta: { requiresAuth: true },
            children: [
                {
                    path: '',
                    name: 'accountdetails-default',
                    component: () => import('@/views/customer/AccountDetails.vue'),
                },
                {
                    path: ':id',
                    name: 'accountdetails-user',
                    component: () => import('@/views/customer/AccountDetails.vue'),
                },
            ],
        },
        {
            path: '/auth',
            name: 'auth',
            meta: { isGuest: true },
            children: [
                {
                    path: 'login',
                    name: 'login',
                    component: () => import('@/views/auth/LoginView.vue'),
                },
                {
                    path: 'register',
                    name: 'register',
                    component: () => import('@/views/auth/RegisterView.vue'),
                },
            ],
        },
        {
            path: '/employee',
            name: 'employee',
            meta: { requiresAuth: true },
            children: [
                {
                    path: '',
                    name: 'employee-dashboard',
                    component: () => import('@/views/employee/EmployeeDashboard.vue'),
                },
                {
                    path: 'customers-overview',
                    name: 'customers-overview',
                    component: () => import('@/views/employee/CustomerOverview.vue'),
                },
                {
                    path: 'customer/:id/approve',
                    name: 'customer-details',
                    component: () => import('@/views/employee/ApproveCustomer.vue'),
                },
                {
                    path: 'customer/:id/limits',
                    name: 'customer-limits',
                    component: () => import('@/views/employee/LimitsCustomer.vue'),
                },
            ],
        },
    ],
});

router.beforeEach((to, from, next) => {
    const store = useUserStore();
    if (to.meta.requiresAuth && !store.isAuthenticated) {
        next({ name: 'auth.login', replace: true });
    } else if (store.isAuthenticated && to.meta.isGuest) {
        next({ name: 'home', replace: true });
    } else {
        next();
    }
});

export default router;
