<template>
    <canvas id="confetti"></canvas>
    <div class="container">
        <div class="atm-card text-center">
            <img src="/images/TechSageCapital-Logo-ATM.svg" alt="Tech Sage Captial Logo" class="mb-4"
                style="width: 250px;">
            <h3 class="mb-4">Welcome to TechSageATM&trade;</h3>
            <div class="mb-3">
                <label for="status" class="form-label">Account:</label>
                <select class="form-select" id="status" v-model="selectedAccount">
                    <option :value="null" disabled selected>
                        <span v-if="bankAccounts?.content.length === 0">No bank acocunts found.</span>
                        <span v-else>Select an account</span>
                    </option>
                    <option v-for="account in bankAccounts?.content" :key="account.id" :value="account">
                        {{ account.iban }}
                    </option>
                </select>
            </div>
            <div v-if="selectedAccount">
                <p>This account has a balance of: {{ formatMoney(selectedAccount.balance) }}</p>
                <VeeForm v-slot="{ handleSubmit }" as="div">
                    <form>
                        <div class="mb-3">
                            <FormInput name="amount" label="Amount" type="number" placeholder="Enter amount" :min="0"
                                :step="0.01" required />
                        </div>
                        <div class="mb-3 d-flex justify-content-between">
                            <button class="btn btn-primary" @click="handleSubmit($event, doWithdraw)">
                                <FontAwesomeIcon :icon="faMoneyBillTransfer" class="me-2" />
                                Withdraw
                            </button>
                            <button class="btn btn-primary" @click="handleSubmit($event, doDeposit)">
                                <FontAwesomeIcon :icon="faMoneyBillTrendUp" class="me-2" />
                                Deposit
                            </button>
                        </div>
                    </form>
                </VeeForm>
            </div>
            <button class="btn btn-danger mt-3" @click="logout">
                Logout
            </button>
        </div>
    </div>
</template>

<script lang="ts" setup>
import axiosClient, { type PaginatedResponse } from '@/axios';
import FormInput from '@/components/forms/FormInput.vue';
import type { BankAccount } from '@/types';
import { formatMoney, processFormError } from '@/utils';
import { faMoneyBillTransfer, faMoneyBillTrendUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { Form as VeeForm, type GenericObject, type SubmissionContext } from 'vee-validate';
import { onBeforeMount, onUnmounted, ref } from 'vue';
import confetti from 'canvas-confetti';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();
const bankAccounts = ref<PaginatedResponse<BankAccount>>();
const selectedAccount = ref<BankAccount | null>(null);
const confettiCanvas = document.getElementById('status') as HTMLCanvasElement;
const statusConfetti = confetti.create(confettiCanvas, { resize: true });

const createConfetti = (shape: confetti.Shape) => {
    statusConfetti({
        shapes: [shape],
        scalar: 10
    });
}

const logout = () => {
    userStore.logout();
    router.push('/');
};

const setNewAccount = (account: BankAccount) => {
    selectedAccount.value = account;
    if (bankAccounts.value === undefined) {
        return;
    }
    bankAccounts.value.content = bankAccounts.value.content.map(a => {
        if (a.id === account.id) {
            a = account
        }
        return a;
    });
}

const doWithdraw = (values: GenericObject, actions: SubmissionContext) => {
    axiosClient.post('/atm/withdraw', {
        withdrawFrom: selectedAccount.value?.iban.replace(/\s/g, ''),
        amount: values.amount
    })
        .then(response => {
            setNewAccount(response.data);
            createConfetti(confetti.shapeFromText({ text: '💶', scalar: 10 }));
        })
        .catch(error => {
            processFormError(error, values, actions);
        });
}

const doDeposit = (values: GenericObject, actions: SubmissionContext) => {
    axiosClient.post('/atm/deposit', {
        depositTo: selectedAccount.value?.iban.replace(/\s/g, ''),
        amount: values.amount
    })
        .then(response => {
            setNewAccount(response.data);
            createConfetti(confetti.shapeFromText({ text: '💸', scalar: 10 }));
        })
        .catch(error => {
            processFormError(error, values, actions);
        });
}

onBeforeMount(() => {
    axiosClient.get<PaginatedResponse<BankAccount>>('/bankAccounts?type=CHECKING')
        .then(response => {
            bankAccounts.value = response.data;
        })
        .catch(error => {
            console.error('Error fetching bank accounts:', error);
        });
});

onUnmounted(() => {
    statusConfetti.reset();
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
