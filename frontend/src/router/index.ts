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
            ]
        }
    ],
});

export default router;
