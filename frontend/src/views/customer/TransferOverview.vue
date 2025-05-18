<template>
    <main>
        <div class="container py-5">
            <h1 class="display-4 fw-bold text-left mb-5">Transfer Overview</h1>
            <div v-if="loading" class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>
            <div v-if="errorMessage" class="alert alert-danger text-center">
                {{ errorMessage }}
            </div>
            <div class="container row mb-4">
                <div class="col-6">
                    <div>
                        <label for="amount" class="form-label">Amount:</label>
                        <input type="number" name="amount" id="amount" class="form-control" v-model="amount">
                    </div>
                    <label for="fromAccount" class="form-label mt-3">From Account:</label>
                    <ul class="list-group">
                        <li class="list-group-item d-flex justify-content-between align-items-center"
                            v-for="account in user?.bankAccounts" :key="account.id">
                            <div>
                                <div>
                                    <strong>Account Type:</strong> {{ account.type }}
                                </div>
                                <div>
                                    <strong>IBAN:</strong> {{ account.iban }}
                                </div>
                                <div>
                                    <strong>Account Balance:</strong> {{ formatMoney(account.balance) }}
                                </div>
                            </div>
                            <button :class="['btn', 'btn-primary', fromAccount === account.iban ? 'btn-success' : '']"
                                @click="selectFromAccount(account)" :disabled="toAccount === account.iban">
                                {{ fromAccount === account.iban ? 'Selected' : 'Select' }}
                            </button>
                        </li>
                    </ul>
                </div>
                <div class="col-6">
                    <div class="d-flex justify-content-between align-items-center">
                        <div class="col-8">
                            <label for="description" class="form-label">Description</label>
                            <textarea name="description" id="description" class="form-control" v-model="description"
                                style="height: 12px;" />
                        </div>
                        <button class="btn btn-primary mt-3" @click="transfer">Transfer</button>
                    </div>
                    <div class="mt-3">
                        <label for="toAccount" class="form-label">To Account:</label>
                        <input type="text" name="toAccount" id="toAccount" class="form-control"
                            placeholder="NLxxINHOxxxxxxxxxx" v-model="toAccount">
                        <label for="toAccount" class="form-label mt-3">To own account:</label>
                        <ul class="list-group">
                            <li class="list-group-item d-flex justify-content-between align-items-center"
                                v-for="account in user?.bankAccounts" :key="account.id">
                                <div>
                                    <div>
                                        <strong>Account Type:</strong> {{ account.type }}
                                    </div>
                                    <div>
                                        <strong>IBAN:</strong> {{ account.iban }}
                                    </div>
                                    <div>
                                        <strong>Account Balance:</strong> {{ formatMoney(account.balance) }}
                                    </div>
                                </div>
                                <button :class="['btn', 'btn-primary', toAccount === account.iban ? 'btn-success' : '']"
                                    @click="selectToAccount(account)" :disabled="fromAccount === account.iban">
                                    {{ toAccount === account.iban ? 'Selected' : 'Select' }}
                                </button>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </main>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import axiosClient from "@/axios";
import type { BankAccount, User } from "@/types";
import type { AxiosError } from "axios";
import { useRoute } from "vue-router";
import { formatMoney } from "@/utils";

const route = useRoute();

const userIdParam = route.params.id;
const user = ref<User | null>(null);
const errorMessage = ref("");
const loading = ref(false);
const fromAccount = ref("");
const toAccount = ref("");
const amount = ref(0);
const description = ref("");

async function fetchUser() {
    loading.value = true;
    errorMessage.value = "";
    try {
        const response = await axiosClient.get<User>(`/users/${userIdParam ?? 'me'}`);
        user.value = response.data;
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

function selectToAccount(account: BankAccount) {
    if (toAccount.value === account.iban) {
        toAccount.value = "";
    } else {
        toAccount.value = account.iban;
    }
}

function selectFromAccount(account: BankAccount) {
    if (fromAccount.value === account.iban) {
        fromAccount.value = "";
    } else {
        fromAccount.value = account.iban;
    }
}

async function transfer() {
    errorMessage.value = "";
    try {
        const result = await axiosClient.post(`/transactions/create`, {
            fromIban: fromAccount.value.replace(/\s/g, ''),
            toIban: toAccount.value.replace(/\s/g, ''),
            amount: amount.value,
            description: description.value
        });
        if (result.status === 201) {
            alert("Transfer successful!");
            fetchUser();
        }
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while transferring funds. " + (error as AxiosError).message; // error.message is for debugging REMOVE LATER
    }
}


onMounted(() => {
    fetchUser();
});
</script>

<style scoped></style>
