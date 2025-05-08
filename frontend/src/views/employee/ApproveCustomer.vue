<template>
    <main>
        <LimitsForm :id header="Approve Customer" button="Approve Customer" @submit="approveCustomer" />
    </main>
</template>

<script setup lang="ts">
import axiosClient from '@/axios';
import LimitsForm from '@/components/LimitsForm.vue';
import { processFormError } from '@/utils';
import type { GenericObject, SubmissionContext } from 'vee-validate';
import { useRoute, useRouter } from 'vue-router';

const id = Number(useRoute().params.id);
const router = useRouter();

function approveCustomer(values: GenericObject, actions: SubmissionContext) {
    axiosClient.post(`/users/${id}/approve`, values)
        .then(() => {
            router.push({ path: '/employee/customers-overview' });
        })
        .catch((error) => processFormError(error, values, actions));
}
</script>
