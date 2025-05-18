<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useUserStore } from "@/stores/user";
import axiosClient from "@/axios";
import type { User } from "@/types";
import { Role } from "@/types";
import type { AxiosError } from "axios";
import { useRoute } from "vue-router";
import { formatMoney } from "@/utils";

const userStore = useUserStore();

const userId = ref(userStore.id);
const user = ref<User | null>(null);
const errorMessage = ref("");
const loading = ref(false);

async function fetchUser() {
    loading.value = true;
    errorMessage.value = "";
    try {
        if (useRoute().params.id && (userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN))) {
            userId.value = Number(useRoute().params.id);
            const response = await axiosClient.get<User>(`/users/${userId.value}`);
            user.value = response.data;
        }
        else {
            const response = await axiosClient.get<User>(`/users/customer?id=${userStore.id}&email=${userStore.email}`);
            user.value = response.data;
        }
        if (!user.value || user.value == null) {
            errorMessage.value = "User not found.";
        }
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while fetching user details. " + (error as AxiosError).message; // error.message is for debugging REMOVE LATER
    } finally {
        loading.value = false;
    }
}

async function softDeleteAccount() {
    if (confirm("Are you sure you want to delete this customer?")) {
        try {
            await axiosClient.delete(`/users/${userStore.id}/softDelete`);
        } catch (error) {
            errorMessage.value = (error as AxiosError).response
                ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
                : "An error occurred while deleting the customer. " + (error as AxiosError).message; // remove error.message later if it's for debugging
        }
    }
}

onMounted(() => {
    fetchUser();
});
</script>

<template>
    <main>
        <div class="container py-5">
            <h1 class="display-4 fw-bold text-left mb-5">Account details</h1>

            <div v-if="loading" class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>

            <div v-if="errorMessage" class="alert alert-danger text-center">
                {{ errorMessage }}
            </div>
            <h2>User Details</h2>
            <div class="container row mb-4">
                <div v-if="user" class="col-12 customer-details">
                    <div class="row mb-3">
                        <div class="col">
                            <strong>Firstname:</strong> {{ user.firstName }}
                        </div>
                        <div class="col">
                            <strong>Lastname:</strong> {{ user.lastName }}
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col">
                            <strong>Email:</strong> {{ user.email }}
                        </div>
                        <div class="col">
                            <strong>Phone Number:</strong> {{ user.phoneNumber }}
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col">
                            <strong>BSN:</strong> {{ user.bsn }}
                        </div>
                        <div class="col">
                            <strong>Status:</strong> {{ user.status }}
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col">
                            <strong>Daily Limit:</strong> &#8364;{{ user.dailyLimit }}
                        </div>
                        <div class="col">
                            <strong>Transfer Limit:</strong> &#8364;{{ user.transferLimit }}
                        </div>
                    </div>

                    <div class="mb-5">
                        <button class="btn btn-primary me-2">
                            <RouterLink class="text-white text-decoration-none" :to="userStore.roles.includes(Role.EMPLOYEE)
                                ? `/accountdetails/edit/${user.id}`
                                : `/accountdetails/edit`">Edit
                            </RouterLink>
                        </button>
                        <button class="btn btn-primary me-2"
                            v-if="userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)">
                            <RouterLink :to="`/employee/customer/${user.id}/limits`"
                                class="text-white text-decoration-none">Edit user limits</RouterLink>
                        </button>
                        <button class="btn btn-danger"
                            v-if="userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)"
                            @click="softDeleteAccount()">
                            Delete
                        </button>

                    </div>

                    <h2>Bank Account Details</h2>
                    <div v-if="user.bankAccounts.length > 0" class="bank-accounts">
                        <div class="row mb-3">
                            <div class="col">
                                <strong>Total Balance:</strong> €{{user.bankAccounts.reduce((acc, account) => acc +
                                    account.balance, 0)}}
                            </div>
                        </div>
                        <ul class="list-group">
                            <li class="list-group-item" v-for="account in user.bankAccounts" :key="account.id">
                                <div>
                                    <strong>Account Type:</strong> {{ account.type }}
                                </div>
                                <div>
                                    <strong>IBAN:</strong> {{ account.iban }}
                                </div>
                                <div>
                                    <strong>Account Balance:</strong> {{ formatMoney(account.balance) }}
                                </div>
                            </li>
                        </ul>
                    </div>
                    <div v-else class="alert alert-info">
                        No bank accounts found for this user.
                    </div>
                </div>
            </div>
        </div>
    </main>
</template>
