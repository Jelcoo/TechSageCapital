<script setup lang="ts">
import { onMounted, ref } from "vue";
import axiosClient from "@/axios";
import type { Transaction } from "@/types";
import type { AxiosError } from "axios";
import { formatDate } from "@/utils/dates";
import { formatIban } from "@/utils/prettyIban";
import { formatMoney } from "@/utils";

const loading = ref(false);
const errorMessage = ref("");
const transactions = ref<Transaction[]>([]);

async function fetchTransactions() {
    loading.value = true;
    errorMessage.value = "";
    try {
        const response = await axiosClient.get<Transaction[]>(`/transactions`);
        if (!response.data || response.data.length === 0) {
            errorMessage.value = "No transactions found.";
        }
        transactions.value = response.data;
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
        <div class="container">
            <h1>Transactions</h1>
            <div v-if="loading" class="spinner-border" role="status">
                <span class="sr-only"></span>
            </div>
            <div v-else-if="errorMessage">{{ errorMessage }}</div>

            <div v-else>
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Initiator</th>
                            <th>From</th>
                            <th>To</th>
                            <th>Description</th>
                            <th class="text-end">Amount</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="transaction in transactions" :key="transaction.id">
                            <td>{{ formatDate(transaction.createdAt) }}</td>
                            <td>{{ transaction.initiator.firstName }} {{
                                transaction.initiator.lastName }}</td>
                            <td>{{ formatIban(transaction.fromAccount?.iban ?? "") }}</td>
                            <td>{{ formatIban(transaction.toAccount?.iban ?? "") }}</td>
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
        </div>
    </main>
</template>
