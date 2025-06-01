<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useUserStore } from "@/stores/user";
import axiosClient from "@/axios";
import type { BankAccount, User } from "@/types";
import { Role } from "@/types";
import type { AxiosError } from "axios";
import { useRoute } from "vue-router";
import { formatMoney } from "@/utils";
import BankAccountComponent from "@/components/BankAccountComponent.vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faArrowUp19, faKey, faMoneyBillTransfer, faPencil, faPersonArrowDownToLine, faPersonArrowUpFromLine, faTrash } from "@fortawesome/free-solid-svg-icons";
import { Modal } from "bootstrap";
import BackButton from "@/components/BackButton.vue";

const userStore = useUserStore();
const route = useRoute();
const userIdParam = route.params.id;
const user = ref<User | null>(null);
const errorMessage = ref("");
const loading = ref(false);
const successMessage = ref("");
const absoluteLimit = ref(0);
const bankAccountId = ref(0);
const updateModal = ref<HTMLElement | null>(null);
let modalInstance: Modal | null = null;

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

async function softDeleteAccount() {
    if (confirm("Are you sure you want to delete this customer?")) {
        try {
            const response = await axiosClient.delete<User>(`/users/${userIdParam}/softDelete`);
            user.value = response.data;
        } catch (error) {
            errorMessage.value = (error as AxiosError).response
                ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
                : "An error occurred while deleting the customer. " + (error as AxiosError).message; // remove error.message later if it's for debugging
        }
    }
}

const openModal = (account: BankAccount): void => {
    modalInstance?.show();
    bankAccountId.value = account.id;
    absoluteLimit.value = account.absoluteMinimumBalance;
};

const closeModal = (): void => {
    modalInstance?.hide();
    bankAccountId.value = 0;
    absoluteLimit.value = 0;
};

async function updateAbsoluteLimit() {
    try {
        errorMessage.value = "";
        successMessage.value = "";
        const response = await axiosClient.put<BankAccount>(`/bankAccounts/${bankAccountId.value}/absoluteLimit`, {
            absoluteMinimumBalance: absoluteLimit.value,
        });
        if (response.status === 200) {
            successMessage.value = "Absolute limit updated successfully.";
        }
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while updating the absolute limit. " + (error as AxiosError).message; // remove error.message later if it's for debugging
    }
    closeModal();
    fetchUser();
};

async function toggleRole() {
    try {
        errorMessage.value = "";
        successMessage.value = "";
        const response = await axiosClient.put<User>(`/users/${userIdParam}/${user.value?.roles.includes(Role.CUSTOMER) ? 'promote' : 'demote'}`);
        if (response.status === 200) {
            successMessage.value = user.value?.roles.includes(Role.CUSTOMER) ? "User promoted to employee successfully." : "User demoted to customer successfully.";
        }
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while updating the role. " + (error as AxiosError).message; // remove error.message later if it's for debugging
    }
    fetchUser();
}

onMounted(() => {
    fetchUser();
    if (updateModal.value) {
        modalInstance = new Modal(updateModal.value);
    }
});
</script>

<template>
    <main>
        <div class="container py-5">
            <BackButton />
            <h1 class="display-4 fw-bold text-left mb-5">Account details</h1>

            <div v-if="loading" class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden"></span>
                </div>
            </div>

            <div v-if="errorMessage" class="alert alert-danger text-center">
                {{ errorMessage }}
            </div>
            <div v-if="successMessage" class="alert alert-success text-center">
                {{ successMessage }}
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
                            <strong>Daily Limit:</strong> {{ formatMoney(user.dailyLimit) }}
                        </div>
                        <div class="col">
                            <strong>Transfer Limit:</strong> {{ formatMoney(user.transferLimit) }}
                        </div>
                    </div>

                    <div class="mb-5 d-flex gap-2 flex-wrap">
                        <button class="btn btn-primary">
                            <RouterLink class="text-white text-decoration-none" :to="userStore.roles.includes(Role.EMPLOYEE)
                                ? `/accountdetails/edit/${user.id}`
                                : `/accountdetails/edit`">
                                <FontAwesomeIcon :icon="faPencil" class="me-2" /> Edit
                            </RouterLink>
                        </button>
                        <button class="btn btn-primary">
                            <RouterLink class="text-white text-decoration-none" :to="userStore.roles.includes(Role.EMPLOYEE)
                                ? `/accountdetails/editpassword/${user.id}`
                                : `/accountdetails/editpassword`">
                                <FontAwesomeIcon :icon="faKey" class="me-2" /> Change password
                            </RouterLink>
                        </button>
                        <button class="btn btn-primary" :disabled="user.bankAccounts.length == 0"
                            v-if="userStore.roles.includes(Role.EMPLOYEE)">
                            <RouterLink :to="`/accountdetails/transfer${userIdParam ? `/${user.id}` : ''}`"
                                class="text-white text-decoration-none">
                                <FontAwesomeIcon :icon="faMoneyBillTransfer" class="me-2" /> Transfer
                            </RouterLink>
                        </button>
                        <button :class="user.roles.includes(Role.CUSTOMER) ? 'btn btn-primary' : 'btn btn-danger'"
                            v-if="userStore.roles.includes(Role.ADMIN) && Number(userIdParam) !== userStore.id && route.path !== '/accountdetails'"
                            @click="toggleRole()">
                            <FontAwesomeIcon
                                :icon="user.roles.includes(Role.CUSTOMER) ? faPersonArrowUpFromLine : faPersonArrowDownToLine"
                                class="me-2" />
                            {{ user.roles.includes(Role.CUSTOMER) ? "Promote to Employee" : "Demote to Customer" }}
                        </button>
                        <button class="btn btn-danger"
                            v-if="userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)"
                            @click="softDeleteAccount()">
                            <FontAwesomeIcon :icon="faTrash" class="me-2" /> Delete
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
                            <BankAccountComponent v-for="account in user.bankAccounts" :key="account.id"
                                :bank-account="account" :show-transactions-button="true">
                                <template #button>
                                    <button class="btn btn-primary" @click="openModal(account)">
                                        <FontAwesomeIcon :icon="faArrowUp19" class="me-2" />Edit absolute limit
                                    </button>
                                </template>
                            </BankAccountComponent>
                        </ul>
                    </div>
                    <div v-else class="alert alert-info">
                        No bank accounts found for this user.
                    </div>
                </div>
            </div>
        </div>
        <div class="modal fade" id="updateModal" tabindex="-1" aria-labelledby="updateModalLabel" aria-hidden="true"
            ref="updateModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="updateModalLabel">Update absolute limit</h5>
                        <button type="button" class="btn-close" @click="closeModal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="absoluteLimit" class="form-label">Absolute Limit</label>
                            <input type="number" class="form-control" id="absoluteLimit" v-model="absoluteLimit">
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" @click="closeModal">Close</button>
                        <button type="button" class="btn btn-primary" @click="updateAbsoluteLimit">Save</button>
                    </div>
                </div>
            </div>
        </div>
    </main>
</template>
