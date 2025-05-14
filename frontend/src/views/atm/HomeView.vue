<template>
    <div class="container">
        <div class="atm-card text-center">
            <img src="/images/TechSageCapital-Logo-ATM.png" alt="Tech Sage Captial Logo" class="mb-4"
                style="width: 250px;">
            <h3 class="mb-4">Welcome to TechSageATM&trade;</h3>
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
    </div>
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

<style scoped>
.atm-card {
    max-width: 400px;
    margin: 80px auto;
    padding: 30px;
    border-radius: 15px;
    background-color: #2c2f33;
}

.btn-atm {
    background-color: #00cba9;
    border: none;
}

.btn-atm:hover {
    background-color: #00a98c;
}
</style>
