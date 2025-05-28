<template>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col col-md-6 col-lg-4 col-xl-3 py-4">
                <main class="form-signin w-100 m-auto">

                    <h1 class="h3 fw-normal">{{ props.header }}</h1>
                    <p class="text-body-secondary">Please set the limits of this customer</p>

                    <VeeForm v-slot="{ handleSubmit }" as="div">
                        <form @submit="handleSubmit($event, onSubmit)">
                            <FormInput name="transferLimit" label="transfer limit" type="number" placeholder="100.00"
                                :value="user?.bankAccounts ? user.transferLimit : 0" />
                            <FormInput name="dailyTransferLimit" label="Daily transfer limit" type="number"
                                placeholder="100.00" :value="user?.bankAccounts ? user.dailyLimit : 0" />
                            <FormInput name="absoluteLimitChecking" label="Absolute limit checking" type="number"
                                placeholder="100.00" :value="0" />
                            <FormInput name="absoluteLimitSavings" label="Absolute limit savings" type="number"
                                placeholder="100.00" :value="0" />
                            <button class="btn btn-primary w-100 py-2 my-3" type="submit">{{ props.button }}</button>
                        </form>
                    </VeeForm>
                </main>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import axiosClient from '@/axios';
import type { User } from '@/types';
import type { AxiosError } from 'axios';
import { onMounted, ref } from 'vue';
import FormInput from '@/components/forms/FormInput.vue';
import { Form as VeeForm, type GenericObject, type SubmissionContext } from 'vee-validate';

const errorMessage = ref("");
const loading = ref(false);
const user = ref<User | null>(null);
const emits = defineEmits(['submit']);
const props = defineProps({
    id: {
        type: Number,
        required: true
    },
    header: {
        type: String,
        required: true
    },
    button: {
        type: String,
        required: true
    }
});

async function fetchUser() {
    loading.value = true;
    errorMessage.value = "";
    try {
        const response = await axiosClient.get<User>(`/users/${props.id}`);
        user.value = response.data;
        if (!user.value || user.value == null) {
            errorMessage.value = "User not found.";
        }
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while fetching user details. " + (error as AxiosError).message; // error.message is for debugging REMOVE LATER
    } finally {
        loading.value = false;
    }
}

const onSubmit = (values: GenericObject, actions: SubmissionContext) => {
    emits('submit', values, actions);
};

onMounted(() => {
    fetchUser();
});

</script>

<style scoped></style>
