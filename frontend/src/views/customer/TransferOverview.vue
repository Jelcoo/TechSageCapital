<template>
    <main>
        <div class="container py-5 col-md-6 col-12">
            <BackButton />
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
                        <BankAccountComponent v-for="account in user?.bankAccounts" :key="account.id"
                            :bankAccount="account"
                            :custom-class="fromAccount?.iban === account.iban ? 'list-group-item-light selected' : ''">
                            <template v-slot:button>
                                <button
                                    :class="['btn', 'btn-primary', fromAccount?.iban === account.iban ? 'btn-success' : '']"
                                    @click="selectFromAccount(account)">
                                    {{ fromAccount?.iban === account.iban ? 'Selected' : 'Select' }}
                                </button>
                            </template>
                        </BankAccountComponent>
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
                                placeholder="NLxxTESCxxxxxxxxxx" v-model="toAccount">
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
                            <BankAccountComponent v-for="account in user?.bankAccounts" :key="account.id"
                                :bankAccount="account"
                                :custom-class="toAccount === account.iban ? 'list-group-item-light selected' : ''">
                                <template v-slot:button>
                                    <button
                                        :class="['btn', 'btn-primary', toAccount === account.iban ? 'btn-success' : '']"
                                        @click="selectToAccount(account)">
                                        {{ toAccount === account.iban ? 'Selected' : 'Select' }}
                                    </button>
                                </template>
                            </BankAccountComponent>
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
                                    <BankAccountComponent v-for="account in user?.bankAccounts" :key="account.id"
                                        :bankAccount="account"
                                        :custom-class="toAccount === account.iban ? 'list-group-item-light selected' : ''">
                                        <template v-slot:button>
                                            <button
                                                :class="['btn', 'btn-primary', toAccount === account.iban ? 'btn-success' : '']"
                                                @click="selectToAccount(account)">
                                                {{ toAccount === account.iban ? 'Selected' : 'Select' }}
                                            </button>
                                        </template>
                                    </BankAccountComponent>
                                </ul>
                            </div>
                            <div class="mt-3">
                                <h5 class="mb-1">Search accounts:</h5>
                                <label for="firstnameSearch" class="form-label">Firstname:</label>
                                <input type="text" name="firstnameSearch" id="firstnameSearch" class="form-control"
                                    v-model="firstName" @input="debouncedSearch">
                                <label for="lastnameSearch" class="form-label">Lastname:</label>
                                <input type="text" name="lastnameSearch" id="lastnameSearch" class="form-control"
                                    v-model="lastName" @input="debouncedSearch">
                            </div>
                            <div class="mt-3">
                                <PageIndicator v-if="searchResults" :pagination="searchResults"
                                    @pageSelect="handlePageSelect">
                                    <ul class="list-group">
                                        <SearchBankAccountComponent v-for="account in searchResults.content"
                                            :key="account.id" :bankAccount="account"
                                            :custom-class="toAccount === account.iban ? 'list-group-item-light selected' : ''">
                                            <template v-slot:button>
                                                <button
                                                    :class="['btn', 'btn-primary', toAccount === account.iban ? 'btn-success' : '']"
                                                    @click="selectToAccount(account)"
                                                    :disabled="fromAccount?.iban === account.iban">
                                                    {{ toAccount === account.iban ? 'Selected' : 'Select' }}
                                                </button>
                                            </template>
                                        </SearchBankAccountComponent>
                                    </ul>
                                </PageIndicator>
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
import axiosClient, { type PaginatedResponse } from "@/axios";
import { BankAccountType, type BankAccount, type SearchResponseBankaccount, type User } from "@/types";
import type { AxiosError } from "axios";
import { useRoute } from "vue-router";
import { Modal } from "bootstrap";
import { useDebounceFn } from "@vueuse/core";
import PageIndicator from "@/components/PageIndicator.vue";
import BankAccountComponent from "@/components/BankAccountComponent.vue";
import BackButton from "@/components/BackButton.vue";
import SearchBankAccountComponent from "@/components/SearchBankAccountComponent.vue";

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
const searchResults = ref<PaginatedResponse<SearchResponseBankaccount> | null>(null);
const searchModal = ref<HTMLElement | null>(null);
let modalInstance: Modal | null = null;
const page = ref(1);

const openModal = (): void => {
    modalInstance?.show();
};

const closeModal = (): void => {
    modalInstance?.hide();
    firstName.value = "";
    lastName.value = "";
    searchResults.value = null;
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

const searchAccounts = async function () {
    errorMessage.value = "";
    try {
        const response = await axiosClient.get<PaginatedResponse<SearchResponseBankaccount>>(`/bankAccounts/find?firstName=${firstName.value}&lastName=${lastName.value}&page=${page.value}`);
        searchResults.value = response.data;
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while searching for accounts. " + (error as AxiosError).message; // error.message is for debugging REMOVE LATER
    }
}

const debouncedSearch = useDebounceFn(() => {
    searchAccounts();
}, 500);

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

function handlePageSelect(pageNumber: number) {
    page.value = pageNumber;
    searchAccounts();
}

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
