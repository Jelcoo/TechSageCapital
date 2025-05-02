<template>
    <nav class="navbar navbar-expand-lg shadow-sm p-3">
        <div class="container">
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <RouterLink to="/" class="nav-link" active-class="active">Home</RouterLink>
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

const router = useRouter();
const userStore = useUserStore();
const isAuthenticated = computed(() => userStore.isAuthenticated);

const logout = () => {
    userStore.logout();
    router.push('/');
};
</script>
