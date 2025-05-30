<script setup lang="ts">
import { onMounted, ref, watch } from "vue";
import { useUserStore } from "@/stores/user";
import axiosClient, { type PaginatedResponse } from "@/axios";
import type { Transaction } from "@/types";
import { useRoute } from "vue-router";
import { Role } from "@/types";
import type { AxiosError } from "axios";
import { formatDate } from "@/utils/dates";
import { formatIban } from "@/utils/prettyIban";
import { formatMoney } from "@/utils";
import BackButton from "@/components/BackButton.vue";
import PageIndicator from "@/components/PageIndicator.vue";

const loading = ref(false);
const errorMessage = ref("");
const transactions = ref<PaginatedResponse<Transaction>>();
const userStore = useUserStore();
const bankAccount = String(useRoute().params.iban);
const bankAccountId = Number(useRoute().params.id);
const page = ref(1);
const startDate = ref("");
const endDate = ref("");
const amountFilterType = ref<string>("");
const amountFilterValue = ref<number | null>(null);
const ibanFilter = ref("");
const showFilters = ref(false);

async function fetchTransactions() {
    loading.value = true;
    errorMessage.value = "";
    try {
        if (userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)) {
            const response = await axiosClient.get<PaginatedResponse<Transaction>>(`/transactions/${bankAccountId}?page=${page.value}`, {
                params: {
                    startDate: startDate.value || null,
                    endDate: endDate.value || null,
                    amountFilterType: amountFilterType.value || null,
                    amountFilterValue: amountFilterValue.value || null,
                    ibanFilter: ibanFilter.value ? ibanFilter.value.replace(/\s+/g, "") : null
                }
            });
            transactions.value = response.data;
        }
        else {
            if (userStore.bankAccounts.some((account) => account.id === bankAccountId)) {
                const response = await axiosClient.get<PaginatedResponse<Transaction>>(`/transactions/${bankAccountId}/me?page=${page.value}`, {
                    params: {
                        startDate: startDate.value || null,
                        endDate: endDate.value || null,
                        amountFilterType: amountFilterType.value || null,
                        amountFilterValue: amountFilterValue.value || null,
                        ibanFilter: ibanFilter.value ? ibanFilter.value.replace(/\s+/g, "") : null
                    }
                });
                transactions.value = response.data;
            } else {
                errorMessage.value = "Bank account not found or you do not have access.";
            }
        }
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

watch([startDate, endDate, amountFilterType, amountFilterValue, ibanFilter], () => {
    fetchTransactions();
});

</script>

<template>
    <main>
        <div class="container py-5">
            <BackButton />
            <h1>Transactions history for {{ bankAccount }}</h1>
            <div v-if="loading || !transactions" class="spinner-border" role="status">
                <span class="sr-only"></span>
            </div>
            <div v-else-if="errorMessage">{{ errorMessage }}</div>
            <template v-else>
                <button class="btn btn-primary mb-3" @click="showFilters = !showFilters">
                    {{ showFilters ? 'Hide Filters' : 'Show Filters' }}
                </button>

                <div class="row mb-3" v-show="showFilters">
                    <div class="col">
                        <label for="startDate" class="form-label">Start Date</label>
                        <input id="startDate" type="date" v-model="startDate" class="form-control" />
                    </div>
                    <div class="col">
                        <label for="endDate" class="form-label">End Date</label>
                        <input id="endDate" type="date" v-model="endDate" class="form-control" />
                    </div>
                    <div class="col">
                        <label for="amountFilterType" class="form-label">Amount Filter</label>
                        <select id="amountFilterType" v-model="amountFilterType" class="form-select">
                            <option default value="">Any</option>
                            <option value="LESS_THAN">Less than</option>
                            <option value="EQUALS">Equal to</option>
                            <option value="GREATER_THAN">Greater than</option>
                        </select>
                    </div>
                    <div class="col">
                        <label for="amountFilterValue" class="form-label">Amount</label>
                        <input id="amountFilterValue" type="number" v-model.number="amountFilterValue"
                            class="form-control" />
                    </div>
                    <div class="col">
                        <label for="ibanFilter" class="form-label">IBAN</label>
                        <input id="ibanFilter" type="text" v-model="ibanFilter" class="form-control"
                            placeholder="Enter IBAN" />
                    </div>
                </div>

                <PageIndicator v-if="transactions" :pagination="transactions" @pageSelect="handlePageSelect">
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
                            <tr v-for="transaction in transactions.content" :key="transaction.id">
                                <td>{{ formatDate(transaction.createdAt) }}</td>
                                <td v-if="transaction.type === 'WITHDRAWAL'">{{ formatIban(transaction.toAccount?.iban
                                    ??
                                    "")
                                    }}</td>
                                <td v-else-if="transaction.type === 'DEPOSIT'">{{
                                    formatIban(transaction.fromAccount?.iban
                                        ?? "") }}</td>
                                <td
                                    v-else-if="transaction.type === 'ATM_WITHDRAWAL' || transaction.type === 'ATM_DEPOSIT'">
                                    ATM
                                </td>
                                <td v-else>Error</td>
                                <td>{{ transaction.description }}</td>
                                <td class="text-danger text-end"
                                    v-if="transaction.type === 'WITHDRAWAL' || transaction.type === 'ATM_WITHDRAWAL'">
                                    <span class="badge text-bg-danger fs-6"> -{{ formatMoney(transaction.amount)
                                    }}</span>
                                </td>
                                <td class="text-success text-end" v-else><span class="badge text-bg-success fs-6"> +{{
                                    formatMoney(transaction.amount) }}</span></td>
                            </tr>
                        </tbody>
                    </table>
                </PageIndicator>
            </template>
        </div>
    </main>
</template>
