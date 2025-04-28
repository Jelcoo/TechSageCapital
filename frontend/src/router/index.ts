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
            path: '/auth',
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
            name: 'about',
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
            ],
        },
    ],
});

export default router;
