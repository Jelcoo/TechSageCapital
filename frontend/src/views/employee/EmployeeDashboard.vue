<template>
    <main>
        <div class="container py-5">
            <h1>Employee Dashboard</h1>
            <p> {{ currentTime }}</p>
            <p>Welcome, {{ userStore.firstName }} {{ userStore.lastName }}</p>
            <p>Let's make that money</p>
            <div class="employee-art mb-4 row w-100">
                <div class="col">
                    <img src="/images/TechSageCapital-employee-incentive.png" alt="Employee Dashboard" />
                    <p class="caption">Wizard Jelcius</p>
                </div>
            </div>
            <RouterLink to="/employee/customers-overview" class="btn btn-primary">Customer Overview</RouterLink>
        </div>
    </main>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
const time = new Date();
const currentTime = time.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
</script>

<style scoped></style>
