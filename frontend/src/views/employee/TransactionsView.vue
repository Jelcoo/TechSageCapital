<script setup lang="ts">
import { onMounted, ref } from "vue";
import axiosClient, { type PaginatedResponse } from "@/axios";
import type { Transaction } from "@/types";
import type { AxiosError } from "axios";
import { formatDate } from "@/utils/dates";
import { formatIban } from "@/utils/prettyIban";
import { formatMoney } from "@/utils";
import PageIndicator from "@/components/PageIndicator.vue";
import BackButton from "@/components/BackButton.vue";

const loading = ref(false);
const errorMessage = ref("");
const transactions = ref<PaginatedResponse<Transaction>>();
const page = ref(1);

async function fetchTransactions() {
    loading.value = true;
    errorMessage.value = "";
    try {
        const response = await axiosClient.get<PaginatedResponse<Transaction>>(`/transactions?page=${page.value}`);
        transactions.value = response.data;
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while fetching transactions. " + (error as AxiosError).message; // error.message is for debugging REMOVE LATER
    } finally {
        loading.value = false;
    }
}

function handlePageSelect(pageNumber: number) {
    page.value = pageNumber;
    fetchTransactions();
}

onMounted(() => {
    fetchTransactions();
});
</script>

<template>
    <main>
        <div class="container py-5">
            <BackButton />
            <h1>Transactions</h1>
            <div v-if="loading || !transactions" class="spinner-border" role="status">
                <span class="sr-only"></span>
            </div>
            <div v-else-if="errorMessage">{{ errorMessage }}</div>

            <PageIndicator v-else :pagination="transactions" @pageSelect="handlePageSelect">
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
                        <tr v-for="transaction in transactions.content" :key="transaction.id">
                            <td>{{ formatDate(transaction.createdAt) }}</td>
                            <td>{{ transaction.initiator.firstName }} {{
                                transaction.initiator.lastName }}</td>
                            <td>{{ formatIban(transaction.fromAccount?.iban ?? "") }}</td>
                            <td>{{ formatIban(transaction.toAccount?.iban ?? "") }}</td>
                            <td>{{ transaction.description }}</td>
                            <td class="text-danger text-end"
                                v-if="transaction.type === 'WITHDRAWAL' || transaction.type === 'ATM_WITHDRAWAL'"><span
                                    class="badge text-bg-danger fs-6"> -{{ formatMoney(transaction.amount)
                                    }}</span>
                            </td>
                            <td class="text-success text-end" v-else><span class="badge text-bg-success fs-6"> +{{
                                formatMoney(transaction.amount) }}</span></td>
                        </tr>
                    </tbody>
                </table>
            </PageIndicator>
        </div>
    </main>
</template>
