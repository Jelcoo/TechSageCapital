<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useUserStore } from "@/stores/user";
import axiosClient from "@/axios";
import type { Transaction } from "@/types";
import { useRoute } from "vue-router";
import { Role } from "@/types";
import type { AxiosError } from "axios";

const loading = ref(false);
const errorMessage = ref("");
const transactions = ref<Transaction[]>([]);
const userStore = useUserStore();
const bankAccountId = Number(useRoute().params.id);

onMounted(() => {
    fetchTransactions();
});
async function fetchTransactions() {
    loading.value = true;
    errorMessage.value = "";
    try {
        if (useRoute().params.id && (userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN))) {
            const response = await axiosClient.get<Transaction>(`/transactions/${bankAccountId.value}`);
            transactions.value = [response.data];
        }
        else {
            if (userStore.bankAccounts.values.id.includes(bankAccountId)) {
                const response = await axiosClient.get<Transaction>(`/transactions/${bankAccountId}`);
                transactions.value = [response.data];
            }
            else {
                errorMessage.value = "Bank account not found.";
            }
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

</script>

<template>
    <main>
        <h1>Transactions history</h1>
        <div v-if="loading" class="spinner-border" role="status">
            <span class="sr-only">Loading...</span>
        </div>
        <div v-else-if="errorMessage">{{ errorMessage }}</div>

        <div v-else>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Type</th>
                        <th>Amount</th>
                        <th>Date</th>
                        <th>From</th>
                        <th>To</th>
                        <th>Description</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="transaction in transactions" :key="transaction.id">
                        <td>{{ transaction.type }}</td>
                        <td>{{ transaction.amount }}</td>
                        <td>{{ transaction.timeStamp }}</td>
                        <td>{{ transaction.from }}</td>
                        <td>{{ transaction.to }}</td>
                        <td>{{ transaction.description }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </main>
</template>