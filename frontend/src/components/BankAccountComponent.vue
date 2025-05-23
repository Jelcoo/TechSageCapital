<script setup lang="ts">
import type { BankAccount } from '@/types';
import { formatMoney } from '@/utils';

const props = defineProps<{
    bankAccount: BankAccount
    showTransactionsButton?: boolean
    customButton?: HTMLElement
}>();
</script>

<template>
    <li class="list-group-item">
        <div>
            <strong>Account Type:</strong> {{ props.bankAccount.type }}
        </div>
        <div>
            <strong>IBAN:</strong> {{ props.bankAccount.iban }}
        </div>
        <div>
            <strong>Account Balance:</strong> {{ formatMoney(props.bankAccount.balance) }}
        </div>
        <div v-if="props.showTransactionsButton">
            <RouterLink :to="`/accountdetails/transactions/${props.bankAccount.id}/${props.bankAccount.iban}`"
                class="btn btn-primary mt-2">
                View
                Transactions</RouterLink>
        </div>
        <div v-if="customButton">{{ customButton }}</div>
    </li>
</template>
