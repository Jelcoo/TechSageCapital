<template>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col col-md-6 col-lg-4 col-xl-3 py-4">
                <main class="form-signin w-100 m-auto">

                    <h1 class="h3 fw-normal">Sign in</h1>
                    <p class="text-body-secondary">Please sign in with your credentials.</p>

                    <ul class="nav nav-underline">
                        <li class="nav-item">
                            <button class="nav-link" :class="{ active: selectedScope === AuthenticationScope.BANK }"
                                @click="selectedScope = AuthenticationScope.BANK">Bank</button>
                        </li>
                        <li class="nav-item">
                            <button class="nav-link" :class="{ active: selectedScope === AuthenticationScope.ATM }"
                                @click="selectedScope = AuthenticationScope.ATM">ATM</button>
                        </li>
                    </ul>

                    <VeeForm v-slot="{ handleSubmit }" as="div">
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
import VueTurnstile from 'vue-turnstile';
import { AuthenticationScope, useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';
import { processFormError } from '@/utils';


const userStore = useUserStore();
const router = useRouter();

const turnstileToken = import.meta.env.VITE_TURNSTILE_KEY;
const turnstileRef = ref('');
const turnstile = useTemplateRef('turnstile');
const selectedScope = ref<AuthenticationScope>(AuthenticationScope.BANK);

const onSubmit = (values: GenericObject, actions: SubmissionContext) => {
    userStore
        .login(values.email, values.password, turnstileRef.value, selectedScope.value)
        .then(() => {
            router.push({ name: 'home' });
        })
        .catch((error) => {
            processFormError(error, values, actions);
            turnstile.value?.reset();
        });
};
</script>
