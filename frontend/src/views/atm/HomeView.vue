<template>
    <main>
        <div class="container py-5">
            <h1 class="display-4 fw-bold text-center mb-4">ATM</h1>

            <div class="mb-3">
                <label for="status" class="form-label">Account:</label>
                <select class="form-select" id="status" v-model="selectedAccount">
                    <option :value="null" disabled selected>
                        <span v-if="bankAccounts.length === 0">No bank acocunts found.</span>
                        <span v-else>Select an account</span>
                    </option>
                    <option v-for="account in bankAccounts" :key="account.id" :value="account">
                        {{ account.iban }}
                    </option>
                </select>
            </div>
            <div v-if="selectedAccount">
                {{ selectedAccount }}
            </div>

        </div>
    </main>
</template>

<script lang="ts" setup>
import axiosClient from '@/axios';
import type { BankAccount } from '@/types';
import { onBeforeMount, ref } from 'vue';

const bankAccounts = ref<BankAccount[]>([]);
const selectedAccount = ref<BankAccount | null>(null);

onBeforeMount(() => {
    axiosClient.get('/atm/bankAccounts')
        .then(response => {
            bankAccounts.value = response.data;
        })
        .catch(error => {
            console.error('Error fetching bank accounts:', error);
        });
});
</script>
