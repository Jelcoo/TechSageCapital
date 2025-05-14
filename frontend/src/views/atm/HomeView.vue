<template>
    Hi
</template>

<script lang="ts" setup>
import type { BankAccount } from '@/types';
import axios from 'axios';
import { onBeforeMount, ref } from 'vue';

const bankAccounts = ref<BankAccount[]>([]);

onBeforeMount(() => {
    axios.get('/atm/bankAccounts')
        .then(response => {
            console.log(response)
            bankAccounts.value = response.data;
        })
        .catch(error => {
            console.error('Error fetching bank accounts:', error);
        });
});
</script>
