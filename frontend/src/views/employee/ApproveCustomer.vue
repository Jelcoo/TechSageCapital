<template>
    <main>
        <div v-if="errorMessage" class="alert alert-danger text-center">
            {{ errorMessage }}
        </div>
        <LimitsForm :id header="Approve Customer" button="Approve Customer" @submit="approveCustomer" />
    </main>
</template>

<script setup lang="ts">
import axiosClient from '@/axios';
import LimitsForm from '@/components/LimitsForm.vue';
import type { AxiosError } from 'axios';
import type { GenericObject } from 'vee-validate';
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const id = Number(useRoute().params.id);
const errorMessage = ref('');
const router = useRouter();


function approveCustomer(values: GenericObject) {
    try {
        axiosClient.post(`/users/${id}/approve`, values).then(({ status }) => {
            if (status === 200) {
                router.push({ path: '/employee/customers-overview' });
            }
        })
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while approving the customer. " + (error as AxiosError).message;
    }
}
</script>

<style lang="scss" scoped></style>
