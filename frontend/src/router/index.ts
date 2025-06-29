import { AuthenticationScope, useUserStore } from '@/stores/user';
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
                    meta: { authorizedRoles: [Role.USER] },
                },
                {
                    path: ':id',
                    name: 'accountdetails-user',
                    component: () => import('@/views/customer/AccountDetails.vue'),
                    meta: { authorizedRoles: [Role.EMPLOYEE] },
                },
                {
                    path: 'edit',
                    name: 'accountdetails-edit-self',
                    component: () => import('@/views/customer/EditAccountDetails.vue'),
                },
                {
                    path: 'edit/:id',
                    name: 'accountdetails-edit-user',
                    component: () => import('@/views/customer/EditAccountDetails.vue'),
                },
                {
                    path: 'transfer/:id',
                    name: 'transfer-employee',
                    component: () => import('@/views/customer/TransferOverview.vue'),
                    meta: { authorizedRoles: [Role.EMPLOYEE] },
                },
                {
                    path: 'transactions/:id/:iban',
                    name: 'transactions-specific',
                    component: () => import('@/views/customer/TransactionList.vue'),
                },
                {
                    path: 'editpassword',
                    name: 'accountdetails-change-own-password',
                    component: () => import('@/views/customer/ChangePassword.vue'),
                },
                {
                    path: 'editpassword/:id',
                    name: 'accountdetails-change-user-password',
                    component: () => import('@/views/customer/ChangePassword.vue'),
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
                    path: 'transactions',
                    name: 'transactions',
                    component: () => import('@/views/employee/TransactionsView.vue'),
                },
            ],
        },
        {
            path: '/transfer',
            name: 'transfer',
            component: () => import('@/views/customer/TransferOverview.vue'),
            meta: { authorizedRoles: [Role.USER] },
        },
        {
            path: '/atm',
            name: 'atm',
            meta: { requiresAuth: true, authorizedRoles: [Role.CUSTOMER] },
            component: () => import('@/views/atm/HomeView.vue'),
        },
        {
            path: '/:catchAll(.*)',
            name: '404',
            component: () => import('@/views/status/NotFoundView.vue'),
        },
    ],
});

router.beforeEach((to, from, next) => {
    const store = useUserStore();

    const requiresAuth = to.meta.requiresAuth;
    const isGuestOnly = to.meta.isGuest;
    const authorizedRoles = to.meta.authorizedRoles as Array<Role> | undefined;

    // Handle regular authentication
    if (requiresAuth && !store.isAuthenticated) {
        return next({ name: 'login', replace: true });
    }

    if (isGuestOnly && store.isAuthenticated) {
        return next({ name: 'home', replace: true });
    }

    // Handle special case for ATM
    if (store.authenticationScope === AuthenticationScope.ATM && to.name !== 'atm') {
        return next({ name: 'atm', replace: true });
    }
    if (store.authenticationScope === AuthenticationScope.BANK && to.name === 'atm') {
        return next({ name: 'home', replace: true });
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
