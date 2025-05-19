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
                        <button class="btn btn-primary mt-3" @click="transfer"
                            :disabled="!fromAccount || !toAccount || amount <= 0">
                            Transfer
                        </button>
                    </div>
                    <div class="mt-3">
                        <label for="toAccount" class="form-label">To Account:</label>
                        <div class="d-flex align-items-center justify-content-between">
                            <input type="text" name="toAccount" id="toAccount" class="form-control w-50"
                                placeholder="NLxxINHOxxxxxxxxxx" v-model="toAccount">
                            <button type="button" class="btn btn-primary" @click="openModal">
                                Search Accounts
                            </button>
                        </div>
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
        <!-- Modal -->
        <div class="modal fade" id="searchModal" tabindex="-1" aria-labelledby="searchModalLabel" aria-hidden="true"
            ref="searchModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="searchModalLabel">Search bankAccounts</h5>
                        <button type="button" class="btn-close" @click="closeModal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div>
                            <label for="firstnameSearch" class="form-label">firstname:</label>
                            <input type="text" name="firstnameSearch" id="firstnameSearch" class="form-control"
                                v-model="firstName" @input="searchAccounts">
                            <label for="lastnameSearch" class="form-label">lastname:</label>
                            <input type="text" name="lastnameSearch" id="lastnameSearch" class="form-control"
                                v-model="lastName" @input="searchAccounts">
                        </div>
                        <div class="mt-3">
                            <ul class="list-group">
                                <li class="list-group-item d-flex justify-content-between align-items-center"
                                    v-for="account in searchResults" :key="account.id">
                                    <div>
                                        <div>
                                            <strong>First Name:</strong> {{ account.firstName }}
                                        </div>
                                        <div>
                                            <strong>Last Name:</strong> {{ account.lastName }}
                                        </div>
                                        <div>
                                            <strong>Account Type:</strong> {{ account.type }}
                                        </div>
                                        <div>
                                            <strong>IBAN:</strong> {{ account.iban }}
                                        </div>
                                    </div>
                                    <button
                                        :class="['btn', 'btn-primary', toAccount === account.iban ? 'btn-success' : '']"
                                        @click="selectToAccount(account)" :disabled="fromAccount === account.iban">
                                        {{ toAccount === account.iban ? 'Selected' : 'Select' }}
                                    </button>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" @click="closeModal">Close</button>
                    </div>
                </div>
            </div>
        </div>
    </main>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import axiosClient from "@/axios";
import type { BankAccount, SearchResponseBankaccount, User } from "@/types";
import type { AxiosError } from "axios";
import { useRoute } from "vue-router";
import { formatMoney } from "@/utils";
import { Modal } from "bootstrap";
import { useDebounceFn } from "@vueuse/core";

const route = useRoute();

const userIdParam = route.params.id;
const user = ref<User | null>(null);
const errorMessage = ref("");
const loading = ref(false);
const fromAccount = ref("");
const toAccount = ref("");
const amount = ref(0);
const description = ref("");
const firstName = ref("");
const lastName = ref("");
const searchResults = ref<SearchResponseBankaccount[]>([]);
const searchModal = ref<HTMLElement | null>(null);
let modalInstance: Modal | null = null;

const openModal = (): void => {
    modalInstance?.show();
};

const closeModal = (): void => {
    modalInstance?.hide();
    firstName.value = "";
    lastName.value = "";
    searchResults.value = [];
};

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

const searchAccounts = useDebounceFn(async () => {
    errorMessage.value = "";
    try {
        const response = await axiosClient.get<SearchResponseBankaccount[]>(`/bankAccounts/find?firstName=${firstName.value}&lastName=${lastName.value}`);
        searchResults.value = response.data;
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while searching for accounts. " + (error as AxiosError).message; // error.message is for debugging REMOVE LATER
    }
}, 500)

function selectToAccount(account: BankAccount | SearchResponseBankaccount) {
    if (toAccount.value === account.iban) {
        toAccount.value = "";
    } else {
        toAccount.value = account.iban;
    }
    closeModal();
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
            amount: amount.value.toFixed(2),
            description: description.value
        });
        if (result.status === 201) {
            alert("Transfer successful!");
            fetchUser();
        }
    } catch (error) {
        const data = (error as AxiosError).response?.data as { message?: string, toIban?: string };
        errorMessage.value = data.message ?? (data.toIban ? `${data.toIban}` : "An unknown error occurred.");
    }
}


onMounted(() => {
    fetchUser();
    if (searchModal.value) {
        modalInstance = new Modal(searchModal.value);
    }
});
</script>

<style scoped></style>
