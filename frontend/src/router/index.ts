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
                    name: 'employee',
                    component: () => import('@/views/employee/EmployeeDashboard.vue'),
                },
                {
                    path: 'customer-overview',``
                    name: 'customer-overview',
                component: () => import('@/views/employee/CustomerOverview.vue'),
                },
    ]
}
    ],
});

export default router;
