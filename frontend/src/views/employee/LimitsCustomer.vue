<template>
    <main>
        <LimitsForm :id header="Set Customer Limits" button="Update Limits" @submit="updateLimits" />
    </main>
</template>

<script setup lang="ts">
import axiosClient from '@/axios';
import LimitsForm from '@/components/LimitsForm.vue';
import type { GenericObject, SubmissionContext } from 'vee-validate';
import { useRoute, useRouter } from 'vue-router';

const id = Number(useRoute().params.id);
const router = useRouter();

function updateLimits(values: GenericObject, actions: SubmissionContext) {
    axiosClient.put(`/users/${id}/limits`, values)
        .then(() => {
            router.back();
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
