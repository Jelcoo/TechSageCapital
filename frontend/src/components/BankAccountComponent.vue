<script setup lang="ts">
import type { BankAccount } from '@/types';
import { formatMoney } from '@/utils';

const props = defineProps<{
    bankAccount: BankAccount
    showTransactionsButton?: boolean
    customClass?: string
}>();
</script>

<template>
    <li class="list-group-item d-flex justify-content-between align-items-center" :class="props.customClass">
        <div class="text-white">
            <div>
                <strong>Account Type:</strong> {{ props.bankAccount.type }}
            </div>
            <div>
                <strong>IBAN:</strong> {{ props.bankAccount.iban }}
            </div>
            <div>
                <strong>Account Balance:</strong> {{ formatMoney(props.bankAccount.balance) }}
            </div>
        </div>
        <div v-if="props.showTransactionsButton">
            <RouterLink :to="`/accountdetails/transactions/${props.bankAccount.id}/${props.bankAccount.iban}`"
                class="btn btn-primary">
                View
                Transactions</RouterLink>
        </div>
        <slot name="button" />
    </li>
</template>
