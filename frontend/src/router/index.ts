import { useUserStore } from '@/stores/user';
import { Role } from '@/types';
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
                    meta: { authorizedRoles: [Role.CUSTOMER] },
                },
                {
                    path: ':id',
                    name: 'accountdetails-user',
                    component: () => import('@/views/customer/AccountDetails.vue'),
                    meta: { authorizedRoles: [Role.EMPLOYEE] },
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
            meta: { requiresAuth: true, authorizedRoles: [Role.EMPLOYEE] },
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
        {
            path: '/atm',
            name: 'atm',
            children: [
                {
                    path: '',
                    name: 'atm-home',
                    component: () => import('@/views/atm/HomeView.vue'),
                    meta: { requiresAtmAuth: true },
                },
                {
                    path: 'login',
                    name: 'atm-login',
                    component: () => import('@/views/atm/LoginView.vue'),
                    meta: { isAtmGuest: true },
                },
            ],
        },
    ],
});

router.beforeEach((to, from, next) => {
    const store = useUserStore();

    const requiresAuth = to.meta.requiresAuth;
    const isGuestOnly = to.meta.isGuest;
    const requiresAtmAuth = to.meta.requiresAtmAuth;
    const isAtmGuestOnly = to.meta.isAtmGuest;
    const authorizedRoles = to.meta.authorizedRoles as Array<Role> | undefined;

    // Handle regular authentication
    if (requiresAuth && !store.isAuthenticated) {
        return next({ name: 'auth.login', replace: true });
    }

    if (isGuestOnly && store.isAuthenticated) {
        return next({ name: 'home', replace: true });
    }

    // Handle ATM-specific authentication
    if (requiresAtmAuth && !store.isAtmAuthenticated) {
        return next({ name: 'atm-login', replace: true });
    }

    if (isAtmGuestOnly && store.isAtmAuthenticated) {
        return next({ name: 'atm-home', replace: true });
    }

    // Role-based access control
    if (authorizedRoles && authorizedRoles.length > 0) {
        const hasAccess = authorizedRoles.some((role) => store.roles.includes(role));
        if (!hasAccess) {
            return next({ name: 'home', replace: true });
        }
    }

    return next();
});

export default router;
