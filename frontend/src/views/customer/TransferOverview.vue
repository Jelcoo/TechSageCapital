<template>
    <main>
        <div class="container py-5 col-6">
            <h1 class="display-4 fw-bold text-left mb-2">Transfer Overview</h1>
            <div v-if="loading" class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>
            <div v-if="errorMessage" class="alert alert-danger text-center">
                {{ errorMessage }}
            </div>
            <div v-if="successMessage" class="alert alert-success text-center">
                {{ successMessage }}
            </div>
            <div class="d-flex flex-column gap-3">
                <div>
                    <label for="fromAccount" class="form-label mt-3">From:</label>
                    <ul class="list-group">
                        <li :class="['list-group-item d-flex justify-content-between align-items-center', fromAccount?.iban === account.iban ? 'list-group-item-light selected' : '']"
                            v-for="account in user?.bankAccounts" :key="account.id">
                            <div class="text-white">
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
                            <button
                                :class="['btn', 'btn-primary', fromAccount?.iban === account.iban ? 'btn-success' : '']"
                                @click="selectFromAccount(account)">
                                {{ fromAccount?.iban === account.iban ? 'Selected' : 'Select' }}
                            </button>
                        </li>
                    </ul>
                </div>
                <div>
                    <div>
                        <label for="amount" class="form-label">Amount:</label>
                        <input type="number" name="amount" id="amount" class="form-control" v-model="amount">
                    </div>
                    <div class="col-12">
                        <label for="description" class="form-label">Description</label>
                        <textarea name="description" id="description" class="form-control" v-model="description"
                            style="height: 12px;" />
                    </div>
                </div>
                <div v-if="fromAccount?.type != BankAccountType.SAVINGS">
                    <div>
                        <label for="toAccount" class="form-label">To:</label>
                        <div class="d-flex align-items-center justify-content-between">
                            <input type="text" name="toAccount" id="toAccount" class="form-control w-75"
                                placeholder="NLxxINHOxxxxxxxxxx" v-model="toAccount">
                            <button type="button" class="btn btn-primary" @click="openModal">
                                Address book
                            </button>
                        </div>
                    </div>
                </div>
                <div v-else>
                    <div>
                        <label for="toAccount" class="form-label">To:</label>
                        <ul class="list-group">
                            <li :class="['list-group-item d-flex justify-content-between align-items-center', toAccount === account.iban ? 'list-group-item-light selected' : '']"
                                v-for="account in user?.bankAccounts.filter(account => account.type != BankAccountType.SAVINGS)"
                                :key="account.id">
                                <div class="text-white">
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
                                    @click="selectToAccount(account)">
                                    {{ toAccount === account.iban ? 'Selected' : 'Select' }}
                                </button>
                            </li>
                        </ul>
                    </div>
                </div>
                <button class="btn btn-primary mt-3" @click="transfer"
                    :disabled="!fromAccount || !toAccount || amount <= 0">
                    Transfer
                </button>
            </div>
            <!-- Modal -->
            <div class="modal fade" id="searchModal" tabindex="-1" aria-labelledby="searchModalLabel" aria-hidden="true"
                ref="searchModal">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="searchModalLabel">Address book</h5>
                            <button type="button" class="btn-close" @click="closeModal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <div>
                                <h5 class="mb-3">To own account:</h5>
                                <ul class="list-group">
                                    <li :class="['list-group-item d-flex justify-content-between align-items-center', toAccount === account.iban ? 'list-group-item-light selected' : '']"
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
                                        <button
                                            :class="['btn', 'btn-primary', toAccount === account.iban ? 'btn-success' : '']"
                                            @click="selectToAccount(account)">
                                            {{ toAccount === account.iban ? 'Selected' : 'Select' }}
                                        </button>
                                    </li>
                                </ul>
                            </div>
                            <div class="mt-3">
                                <h5 class="mb-1">Search accounts:</h5>
                                <label for="firstnameSearch" class="form-label">Firstname:</label>
                                <input type="text" name="firstnameSearch" id="firstnameSearch" class="form-control"
                                    v-model="firstName" @input="searchAccounts">
                                <label for="lastnameSearch" class="form-label">Lastname:</label>
                                <input type="text" name="lastnameSearch" id="lastnameSearch" class="form-control"
                                    v-model="lastName" @input="searchAccounts">
                            </div>
                            <div class="mt-3">
                                <ul class="list-group">
                                    <li :class="['list-group-item d-flex justify-content-between align-items-center', toAccount === account.iban ? 'list-group-item-light selected' : '']"
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
                                            @click="selectToAccount(account)"
                                            :disabled="fromAccount?.iban === account.iban">
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
        </div>
    </main>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from "vue";
import axiosClient from "@/axios";
import { BankAccountType, type BankAccount, type SearchResponseBankaccount, type User } from "@/types";
import type { AxiosError } from "axios";
import { useRoute } from "vue-router";
import { formatMoney } from "@/utils";
import { Modal } from "bootstrap";
import { useDebounceFn } from "@vueuse/core";

const route = useRoute();

const userIdParam = route.params.id;
const user = ref<User | null>(null);
const errorMessage = ref("");
const successMessage = ref("");
const loading = ref(false);
const fromAccount = ref<BankAccount | null>(null);
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
        toAccount.value = '';
    } else {
        toAccount.value = account.iban;
    }
    closeModal();
}

function selectFromAccount(account: BankAccount) {
    if (fromAccount.value?.iban === account.iban) {
        fromAccount.value = null;
    } else {
        fromAccount.value = account;
    }
}

async function transfer() {
    errorMessage.value = "";
    try {
        const result = await axiosClient.post(`/transactions/create`, {
            fromIban: fromAccount.value?.iban.replace(/\s/g, ''),
            toIban: toAccount.value.replace(/\s/g, ''),
            amount: amount.value.toFixed(2),
            description: description.value
        });
        if (result.status === 201) {
            successMessage.value = "Transfer successful.";
            fetchUser();
        }
    } catch (error) {
        const data = (error as AxiosError).response?.data as { message?: string, toIban?: string };
        errorMessage.value = data.message ?? (data.toIban ? `${data.toIban}` : "An unknown error occurred.");
    }
}

watch(fromAccount, (newFromAccount, oldFromAccount) => {
    if (newFromAccount?.type === BankAccountType.SAVINGS && user.value) {
        const availableToAccounts = user.value.bankAccounts.filter(account => account.type != BankAccountType.SAVINGS)
        if (availableToAccounts.length === 1) {
            toAccount.value = availableToAccounts[0].iban;
        } else {
            toAccount.value = '';
        }
    } else if (
        oldFromAccount?.type === BankAccountType.SAVINGS &&
        (!newFromAccount || newFromAccount.type !== BankAccountType.SAVINGS)
    ) {
        // Deselecting a savings account
        toAccount.value = '';
    }
});




onMounted(() => {
    fetchUser();
    if (searchModal.value) {
        modalInstance = new Modal(searchModal.value);
    }
});
</script>

<style scoped>
.selected {
    border-color: green;
}

.list-group-item {
    border-width: 1px;
}
</style>
