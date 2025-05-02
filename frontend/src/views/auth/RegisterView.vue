<template>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col col-md-6 col-lg-4 col-xl-3 py-4">
                <main class="form-signin w-100 m-auto">

                    <h1 class="h3 fw-normal">Register account</h1>
                    <p class="text-body-secondary">Please fill in all details below.</p>

                    <VeeForm v-slot="{ handleSubmit }" :validation-schema="validationSchema" as="div">
                        <form @submit="handleSubmit($event, onSubmit)">
                            <FormInput name="first_name" label="First name" type="text" placeholder="First name" />

                            <FormInput name="last_name" label="Last name" type="text" placeholder="Last name" />

                            <FormInput name="email" label="Email" type="text" placeholder="Email address" />

                            <FormInput name="phone_number" label="Phone number" type="text"
                                placeholder="Phone number" />

                            <FormInput name="bsn" label="BSN" type="text" placeholder="BSN" />

                            <FormInput name="password" label="Password" type="password" placeholder="Password" />

                            <FormInput name="password_confirmation" label="Password confirmation" type="password"
                                placeholder="Password confirmation" />

                            <VueTurnstile ref="turnstile" :site-key="turnstileToken" v-model="turnstileRef" />

                            <button class="btn btn-primary w-100 py-2 my-3" type="submit">Confirm registration</button>
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
    first_name: yup.string().required(),
    last_name: yup.string().required(),
    email: yup.string().required().email(),
    phone_number: yup.string().required(),
    bsn: yup.string().required(),
    password: yup.string().required(),
    password_confirmation: yup.string().oneOf([yup.ref('password'), undefined], 'Passwords must match'),
});

const userStore = useUserStore();
const router = useRouter();

const turnstileToken = import.meta.env.VITE_TURNSTILE_KEY;
const turnstileRef = ref('');
const turnstile = useTemplateRef('turnstile');

const onSubmit = (values: GenericObject, actions: SubmissionContext) => {
    userStore
        .register(
            values.first_name,
            values.last_name,
            values.email,
            values.phone_number,
            values.bsn,
            values.password,
            turnstileRef.value
        )
        .then(() => {
            router.push({ name: 'home' });
        })
        .catch((error) => {
            actions.setErrors({
                password: error.response.data.message,
            });
            turnstile.value?.reset();
        });
};
</script>
