<template>
    <main>
        <LimitsForm :id header="Set Customer Limits" button="Update Limits" @submit="updateLimits" />
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

function updateLimits(values: GenericObject, actions: SubmissionContext) {
    axiosClient.put(`/users/${id}/limits`, values)
        .then(() => {
            router.back();
        })
        .catch((error) => processFormError(error, values, actions));
}
</script>
