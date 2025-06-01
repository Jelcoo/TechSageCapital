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
const showFilters = ref(false);
const startDate = ref("");
const endDate = ref("");
const amountFilterType = ref("");
const amountFilterValue = ref<number | null>(null);
const fromIbanFilter = ref("");
const toIbanFilter = ref("");

async function fetchTransactions() {
    loading.value = true;
    errorMessage.value = "";
    try {
        const response = await axiosClient.get<PaginatedResponse<Transaction>>(`/transactions?page=${page.value}`, {
            params: {
                startDate: startDate.value || null,
                endDate: endDate.value || null,
                amountFilterType: amountFilterType.value || null,
                amountFilterValue: amountFilterValue.value || null,
                fromIbanFilter: fromIbanFilter.value ? fromIbanFilter.value.replace(/\s+/g, "") : null,
                toIbanFilter: toIbanFilter.value ? toIbanFilter.value.replace(/\s+/g, "") : null
            }
        });
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

function applyFilters() {
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
            <button class="btn btn-primary mb-3" @click="showFilters = !showFilters">
                {{ showFilters ? 'Hide Filters' : 'Show Filters' }}
            </button>
            <div class="mb-3" v-show="showFilters">
                <div class="row">
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
                            <option value="">Any</option>
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
                        <label for="fromIbanFilter" class="form-label">From IBAN</label>
                        <input id="fromIbanFilter" type="text" v-model="fromIbanFilter" class="form-control"
                            placeholder="From IBAN" />
                    </div>
                    <div class="col">
                        <label for="toIbanFilter" class="form-label">To IBAN</label>
                        <input id="toIbanFilter" type="text" v-model="toIbanFilter" class="form-control"
                            placeholder="To IBAN" />
                    </div>
                </div>
                <div class="row mt-3">
                    <div class="col d-flex align-items-end">
                        <button class="btn btn-primary" @click="applyFilters">Apply</button>
                    </div>
                </div>
            </div>
            <PageIndicator v-if="transactions" :pagination="transactions" @pageSelect="handlePageSelect">
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
                        <tr v-for="transaction in transactions.content" v-show="transactions" :key="transaction.id">
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
