<script setup lang="ts">
import type { SearchResponseBankaccount } from '@/types';

const props = defineProps<{
    bankAccount: SearchResponseBankaccount
    customClass?: string
}>();
</script>

<template>
    <li class="list-group-item d-flex justify-content-between align-items-center" :class="props.customClass">
        <div class="text-white">
            <div>
                <strong>Name:</strong> {{ props.bankAccount.firstName }} {{ props.bankAccount.lastName }}
            </div>
            <div>
                <strong>IBAN:</strong> {{ props.bankAccount.iban }}
            </div>
        </div>
        <div class="d-flex align-items-center gap-2">
            <slot name="button" />
        </div>
    </li>
</template>
