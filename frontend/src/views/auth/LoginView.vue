<template>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col col-md-6 col-lg-4 col-xl-3 py-4">
                <main class="form-signin w-100 m-auto">

                    <h1 class="h3 fw-normal">Sign in</h1>
                    <p class="text-body-secondary">Please sign in with your credentials.</p>

                    <VeeForm v-slot="{ handleSubmit }" :validation-schema="validationSchema" as="div">
                        <form @submit="handleSubmit($event, onSubmit)">
                            <FormInput name="email" label="Email" type="text" placeholder="Email address" />

                            <FormInput name="password" label="Password" type="password" placeholder="Password" />

                            <VueTurnstile ref="turnstile" :site-key="turnstileToken" v-model="turnstileRef" />

                            <button class="btn btn-primary w-100 py-2 my-3" type="submit">Sign in</button>
                        </form>
                    </VeeForm>
                </main>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import FormInput from '@/components/forms/FormInput.vue';
import { Form as VeeForm, type GenericObject, type SubmissionContext } from 'vee-validate';
import { ref, useTemplateRef } from 'vue';
import * as yup from 'yup';
import VueTurnstile from 'vue-turnstile';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';

const validationSchema = yup.object({
    email: yup.string().required().email(),
    password: yup.string().required(),
});

const userStore = useUserStore();
const router = useRouter();

const turnstileToken = import.meta.env.VITE_TURNSTILE_KEY;
const turnstileRef = ref('');
const turnstile = useTemplateRef('turnstile');

const onSubmit = (values: GenericObject, actions: SubmissionContext) => {
    userStore
        .login(values.email, values.password, turnstileRef.value)
        .then(() => {
            router.push({ name: 'home' });
        })
        .catch((error) => {
            if (error.response.status === 400) {
                if (error.response.data.message) {
                    actions.setErrors({ password: error.response.data.message });
                } else {
                    actions.setErrors(error.response.data);
                }
            } else {
                console.error(error);
                actions.setErrors({
                    password: 'An error occurred. Please try again later.',
                });
            }
            turnstile.value?.reset();
        });
};
</script>
