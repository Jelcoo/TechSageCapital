<template>
    <div class="container">
        <div class="atm-card text-center">
            <img src="/images/TechSageCapital-Logo-ATM.png" alt="Tech Sage Captial Logo" class="mb-4"
                style="width: 250px;">
            <h3 class="mb-4">Welcome to TechSageATM&trade;</h3>
            <p class="mb-4">Please enter your login information to access your ATM account.</p>
            <VeeForm v-slot="{ handleSubmit }" as="div">
                <form @submit="handleSubmit($event, onSubmit)">
                    <FormInput name="email" label="Email" type="text" placeholder="Email address" />

                    <FormInput name="password" label="Password" type="password" placeholder="Password" />

                    <VueTurnstile ref="turnstile" :site-key="turnstileToken" v-model="turnstileRef" />

                    <button class="btn btn-primary w-100 py-2 my-3" type="submit">Sign in</button>
                </form>
            </VeeForm>
        </div>
    </div>
</template>

<script lang="ts" setup>
import FormInput from '@/components/forms/FormInput.vue';
import { Form as VeeForm, type GenericObject, type SubmissionContext } from 'vee-validate';
import { ref, useTemplateRef } from 'vue';
import VueTurnstile from 'vue-turnstile';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';
import { processFormError } from '@/utils';

const userStore = useUserStore();
const router = useRouter();

const turnstileToken = import.meta.env.VITE_TURNSTILE_KEY;
const turnstileRef = ref('');
const turnstile = useTemplateRef('turnstile');

const onSubmit = (values: GenericObject, actions: SubmissionContext) => {
    userStore
        .atmLogin(values.email, values.password, turnstileRef.value)
        .then(() => {
            router.push({ name: 'atm-home' });
        })
        .catch((error) => {
            processFormError(error, values, actions);
            turnstile.value?.reset();
        });
};
</script>

<style scoped>
.atm-card {
    max-width: 400px;
    margin: 80px auto;
    padding: 30px;
    border-radius: 15px;
    background-color: #2c2f33;
}

.btn-atm {
    background-color: #00cba9;
    border: none;
}

.btn-atm:hover {
    background-color: #00a98c;
}
</style>
