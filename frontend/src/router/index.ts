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
            path: '/customers',
            name: 'customers',
            component: () => import('@/views/employee/dashboard/Customers.vue'),
        },
        {
            path: '/employees',
            name: 'employees',
            component: () => import('@/views/admin/Employees.vue'),
        },
        {
            path: '/accountdetails',
            name: 'accountdetails',
            component: () => import('@/views/customer/AccountDetails.vue'),
        },
        {
            path: '/administrationDashboard',
            name: 'administrationDashboard',
            component: () => import('@/views/employee/dashboard/AdminDashboard.vue'),
        }
    ],
});

export default router;
