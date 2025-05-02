<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useUserStore } from "@/stores/user";
import axiosClient from "@/axios";
import type { User } from "../../../src/types/user";
import type { BankAccount } from "../../../src/types/bankAccount";
import { Role } from "../../../src/types/user";
import type { AxiosError } from "axios";

const Store = useUserStore();
const user = ref<User | null>(null);
const BankAccount = ref<BankAccount | null>(null);
const errorMessage = ref("");
const loading = ref(false);

async function fetchUser() {
    loading.value = true;
    errorMessage.value = "";
    try {
        const userId = Store.id;
        const response = await axiosClient.get<User>(`/users/getById/${userId}`);
        user.value = response.data;
        if (!user.value || user.value == null) {
            errorMessage.value = "User not found.";
        }
    } catch (error) {
        errorMessage.value = "An error occurred while fetching user details.";
    } finally {
        loading.value = false;
    }
}

function editAccount() {
    console.log("Edit customer with ID:", Store.id); //debug line
    //go to edit page
}

async function softDeleteAccount() {
    if (confirm("Are you sure you want to delete this customer?")) {
        try {
            const Id = Store.id;
            await axiosClient.put(`/users/softDelete/${Id}`); //patch because soft delete
        } catch (error) {
            const err = error as AxiosError;
            errorMessage.value = err.response
                ? (err.response.data as { message: string }).message
                : "An error occurred while deleting the customer. " + err.message; // remove err.message later is a debugging line
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
            <h1 class="display-4 fw-bold text-center mb-4">Account details</h1>

            <div v-if="loading" class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>

            <div v-if="errorMessage" class="alert alert-danger text-center">
                {{ errorMessage }}
            </div>

            <div v-if="user" class="customer-details">
                <div class="mb-3">
                    <strong>Firstname:</strong> {{ user.firstName }}
                </div>
                <div class="mb-3">
                    <strong>Lastname:</strong> {{ user.lastName }}
                </div>
                <div class="mb-3">
                    <strong>Email:</strong> {{ user.email }}
                </div>
                <div class="mb-3">
                    <strong>Phone Number:</strong> {{ user.phoneNumber }}
                </div>
                <div class="mb-3">
                    <strong>BSN:</strong> {{ user.bsn }}
                </div>
                <div class="mb-3">
                    <strong>Daily Limit:</strong> €{{ user.dailyLimit }}
                </div>
                <div class="mb-3">
                    <strong>Transfer Limit:</strong> €{{ user.transferLimit }}
                </div>
                <div class="mb-3">
                    <strong>Status:</strong> {{ user.status }}
                </div>
                <div v-if="user.bankAccounts.length > 0" class="bank-accounts">
                    <div class="mb-3">
                        <strong>Total Balance:</strong> €{{user.bankAccounts.reduce((acc, account) => acc +
                            account.balance, 0)}}
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
                                <strong>Account Balance:</strong> €{{ account.balance }}
                            </div>
                        </li>
                    </ul>
                </div>

                <div class="mt-4">
                    <button class="btn btn-primary me-2" @click="editAccount()">
                        Edit
                    </button>
                    <button class="btn btn-danger" v-if="Store.roles.includes(Role.EMPLOYEE || Role.ADMIN)"
                        @click="softDeleteAccount()">
                        Delete
                    </button>
                </div>

            </div>
        </div>
    </main>
</template>