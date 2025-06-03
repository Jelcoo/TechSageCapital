<script setup lang="ts">
import type { BankAccount, SearchResponseBankaccount } from '@/types';
import { formatMoney } from '@/utils';

const props = defineProps<{
    bankAccount: BankAccount | SearchResponseBankaccount
    showTransactionsButton?: boolean
    customClass?: string
}>();
</script>

<template>
    <li class="list-group-item d-flex justify-content-between align-items-center" :class="props.customClass">
        <div class="text-white">
            <div v-if="'firstName' in props.bankAccount && 'lastName' in props.bankAccount">
                <strong>Name:</strong> {{ props.bankAccount.firstName }} {{ props.bankAccount.lastName }}
            </div>
            <div>
                <strong>Account Type:</strong> {{ props.bankAccount.type }}
            </div>
            <div>
                <strong>IBAN:</strong> {{ props.bankAccount.iban }}
            </div>
            <div v-if="'balance' in props.bankAccount">
                <strong>Account Balance:</strong> {{ formatMoney(props.bankAccount.balance) }}
            </div>
        </div>
        <div class="d-flex align-items-center gap-2">
            <div v-if="props.showTransactionsButton">
                <RouterLink :to="`/accountdetails/transactions/${props.bankAccount.id}/${props.bankAccount.iban}`"
                    class="btn btn-primary">
                    View
                    Transactions</RouterLink>
            </div>
            <slot name="button" />
        </div>
    </li>
</template>
