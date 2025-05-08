<template>
    <main>
        <LimitsForm :id header="Approve Customer" button="Approve Customer" @submit="approveCustomer" />
    </main>
</template>

<script setup lang="ts">
import axiosClient from '@/axios';
import LimitsForm from '@/components/LimitsForm.vue';
import type { GenericObject, SubmissionContext } from 'vee-validate';
import { useRoute, useRouter } from 'vue-router';

const id = Number(useRoute().params.id);
const router = useRouter();

function approveCustomer(values: GenericObject, actions: SubmissionContext) {
    axiosClient.post(`/users/${id}/approve`, values)
        .then(() => {
            router.push({ path: '/employee/customers-overview' });
        })
        .catch((error) => {
            if (error.response.data.message) {
                const fieldNames = Object.keys(values);
                const lastField = fieldNames[fieldNames.length - 1];
                actions.setErrors({ [lastField]: error.response.data.message });
            } else {
                actions.setErrors(error.response.data);
            }
        });
}
</script>
