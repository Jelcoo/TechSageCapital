<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useUserStore } from "@/stores/user";
import axiosClient from "@/axios";
import type { Transaction } from "@/types";
import { useRoute } from "vue-router";
import { Role } from "@/types";
import type { AxiosError } from "axios";
import { formatDate } from "@/utils/dates";
import { formatIban } from "@/utils/prettyIban";
import { formatMoney } from "@/utils";

const loading = ref(false);
const errorMessage = ref("");
const transactions = ref<Transaction[]>([]);
const userStore = useUserStore();
const bankAccount = String(useRoute().params.iban);
const bankAccountId = Number(useRoute().params.id);

async function fetchTransactions() {
    loading.value = true;
    errorMessage.value = "";
    try {
        if (userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)) {
            const response = await axiosClient.get<Transaction[]>(`/transactions/${bankAccountId}`);
            if (!response.data) {
                errorMessage.value = "No transactions found.";
            }
            console.log(response.data);
            transactions.value = response.data;
        }
        else {
            if (userStore.bankAccounts.some((account) => account.id === bankAccountId)) {
                const response = await axiosClient.get<Transaction[]>(`/transactions/${bankAccountId}/me`);
                transactions.value = response.data;
            } else {
                errorMessage.value = "Bank account not found or you do not have access.";
            }
            errorMessage.value = "Bank account not found.";
        }
        if (!transactions.value || transactions.value.length === 0) {
            errorMessage.value = "No transactions found.";
        }
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while fetching transactions. " + (error as AxiosError).message; // error.message is for debugging REMOVE LATER
    } finally {
        loading.value = false;
    }
}

onMounted(() => {
    fetchTransactions();
});

</script>

<template>
    <main>
        <h1>Transactions history for {{ bankAccount }}</h1>
        <div v-if="loading" class="spinner-border" role="status">
            <span class="sr-only"></span>
        </div>
        <div v-else-if="errorMessage">{{ errorMessage }}</div>

        <div v-else>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Account</th>
                        <th>Description</th>
                        <th class="text-end">Amount</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="transaction in transactions" :key="transaction.id">

                        <td>{{ formatDate(transaction.createdAt) }}</td>
                        <td v-if="transaction.type === 'WITHDRAWAL'">{{ formatIban(transaction.toAccount?.iban ?? "")
                        }}</td>
                        <td v-else-if="transaction.type === 'DEPOSIT'">{{ formatIban(transaction.fromAccount?.iban
                            ?? "") }}</td>
                        <td v-else-if="transaction.type === 'ATM_WITHDRAWAL' || transaction.type === 'ATM_DEPOSIT'">ATM
                        </td>
                        <td v-else>Error</td>
                        <td>{{ transaction.description }}</td>
                        <td class="text-danger text-end"
                            v-if="transaction.type === 'WITHDRAWAL' || transaction.type === 'ATM_WITHDRAWAL'">-{{
                                formatMoney(transaction.amount) }}
                        </td>
                        <td class="text-success text-end" v-else>+{{ formatMoney(transaction.amount) }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </main>
</template>
