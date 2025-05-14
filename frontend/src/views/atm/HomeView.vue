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
                <p>This account has a balance of: {{ formatMoney(selectedAccount.balance) }}</p>
                <form>
                    <div class="mb-3">
                        <label for="amount" class="form-label">Amount:</label>
                        <input type="number" class="form-control" id="amount" v-model="mutationAmount"
                            placeholder="Enter amount" min="0" step="0.01" required />
                    </div>
                </form>
                <div v-if="errorMessage" class="alert alert-danger text-center">
                    {{ errorMessage }}
                </div>
                <div class="mb-3 d-flex justify-content-between">
                    <button class="btn btn-primary" @click="doWithdraw">
                        <FontAwesomeIcon :icon="faMoneyBillTransfer" class="me-2" />
                        Withdraw
                    </button>
                    <button class="btn btn-primary" @click="doDeposit">
                        <FontAwesomeIcon :icon="faMoneyBillTrendUp" class="me-2" />
                        Deposit
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import axiosClient from '@/axios';
import type { BankAccount } from '@/types';
import { formatMoney } from '@/utils';
import { faMoneyBillTransfer, faMoneyBillTrendUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { onBeforeMount, ref } from 'vue';

const bankAccounts = ref<BankAccount[]>([]);
const selectedAccount = ref<BankAccount | null>(null);
const mutationAmount = ref<number>(0);
const errorMessage = ref<string | null>(null);

const setNewAccount = (account: BankAccount) => {
    selectedAccount.value = account;
    mutationAmount.value = 0;
    bankAccounts.value = bankAccounts.value.map(a => {
        if (a.id === account.id) {
            a = account
        }
        return a;
    });
}

const doWithdraw = () => {
    axiosClient.post('/atm/withdraw', {
        withdrawFrom: selectedAccount.value?.iban.replace(/\s/g, ''),
        amount: mutationAmount.value
    })
        .then(response => {
            setNewAccount(response.data);
        })
        .catch(error => {
            errorMessage.value = error.response?.data?.message || 'An error occurred';
        });
}

const doDeposit = () => {
    axiosClient.post('/atm/deposit', {
        depositTo: selectedAccount.value?.iban.replace(/\s/g, ''),
        amount: mutationAmount.value
    })
        .then(response => {
            setNewAccount(response.data);
        })
        .catch(error => {
            errorMessage.value = error.response?.data?.message || 'An error occurred';
        });
}

onBeforeMount(() => {
    axiosClient.get('/bankAccounts?type=CHECKING')
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
</style>
