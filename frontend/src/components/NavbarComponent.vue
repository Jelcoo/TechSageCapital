<template>
    <nav class="navbar navbar-expand-lg shadow-sm p-3">
        <div class="container">
            <div class="collapse navbar-collapse" id="navbarNav">
                <RouterLink to="/" class="nav-link" active-class="active">
                    <img class="nav-logo" alt="TechSage Capital logo" src="/images/TechSageCapita-Logo.svg" />
                </RouterLink>
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
                            <li>
                                <hr class="dropdown-divider">
                            </li>
                            <li>
                                <RouterLink to="/employee/transactions" class="dropdown-item" active-class="active">
                                    Transactions</RouterLink>
                            </li>
                        </ul>
                    </li>
                    <li class="nav-item"
                        v-if="userStore.roles.includes(Role.CUSTOMER) && userStore.status == AccountStatus.ACTIVE">
                        <RouterLink to="/transfer" class="nav-link" active-class="active">Transfer</RouterLink>
                    </li>
                    <li class="nav-item">
                        <RouterLink to="/atm" class="nav-link" active-class="active">ATM</RouterLink>
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
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                            aria-expanded="false">
                            Account
                        </a>
                        <ul class="dropdown-menu">
                            <li>
                                <RouterLink to="/accountdetails" class="dropdown-item p-2" exact-active-class="active">
                                    Account details
                                </RouterLink>
                            </li>
                            <li>
                                <hr class="dropdown-divider">
                            </li>
                            <li>
                                <button class="nav-link p2" @click="logout">Logout</button>
                            </li>
                        </ul>
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
import { AccountStatus, Role } from '@/types';

const router = useRouter();
const userStore = useUserStore();
const isAuthenticated = computed(() => userStore.isAuthenticated);

const logout = () => {
    userStore.logout();
    router.push('/');
};
</script>
