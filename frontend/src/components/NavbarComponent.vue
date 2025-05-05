<template>
    <nav class="navbar navbar-expand-lg shadow-sm p-3">
        <div class="container">
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <RouterLink to="/" class="nav-link" active-class="active">Home</RouterLink>
                    </li>
                    <li class="nav-item dropdown" v-if="isAuthenticated && userStore.roles.includes(Role.EMPLOYEE)">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                            aria-expanded="false">
                            Employee
                        </a>
                        <ul class="dropdown-menu">
                            <li>
                                <RouterLink to="/employee" class="dropdown-item" exact-active-class="active">Home
                                </RouterLink>
                            </li>
                            <li>
                                <hr class="dropdown-divider">
                            </li>
                            <li>
                                <RouterLink to="/employee/customers-overview" class="dropdown-item"
                                    active-class="active">
                                    Customer Overview</RouterLink>
                            </li>
                        </ul>
                    </li>

                </ul>
                <ul class="navbar-nav ms-auto" v-if="!isAuthenticated">
                    <li class="nav-item">
                        <RouterLink to="/auth/login" class="nav-link" active-class="active">Login</RouterLink>
                    </li>
                    <li class="nav-item">
                        <RouterLink to="/auth/register" class="nav-link" active-class="active">Register</RouterLink>
                    </li>
                </ul>
                <ul class="navbar-nav ms-auto" v-else>
                    <li class="nav-item">
                        <button class="nav-link" @click="logout">Logout</button>
                    </li>
                </ul>
            </div>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
        </div>
    </nav>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { computed } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { Role, type User } from '@/types';

const router = useRouter();
const userStore = useUserStore();
const isAuthenticated = computed(() => userStore.isAuthenticated);

const logout = () => {
    userStore.logout();
    router.push('/');
};
</script>
