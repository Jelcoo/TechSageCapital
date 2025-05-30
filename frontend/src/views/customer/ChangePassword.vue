<script setup lang="ts">
import axiosClient from "@/axios";
import { ref, computed } from "vue";
import FormInput from '@/components/forms/FormInput.vue';
import { useUserStore } from '@/stores/user';
import type { AxiosError } from 'axios';
import { Role } from "@/types";
import { useRoute } from "vue-router";
import BackButton from "@/components/BackButton.vue";

const route = useRoute();
const userIdParam = parseInt(route.params.id as string);
const userStore = useUserStore();
const userId = ref(userStore.id);
const errorMessage = ref("");
const successMessage = ref("");
const loading = ref(false);

const editUserPassword = async (event: Event) => {
    event.preventDefault();
    loading.value = true;
    errorMessage.value = "";
    try {
        await axiosClient.put(`/users/${userIdParam}/updatePassword`, {
            newPassword: (event.target as HTMLFormElement).newPassword.value,
            confirmNewPassword: (event.target as HTMLFormElement).confirmNewPassword.value
        });
        successMessage.value = "User password updated successfully.";
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while editing user password. " + (error as AxiosError).message;
    } finally {
        loading.value = false;
    }
};



const editOwnPassword = async (event: Event) => {
    event.preventDefault();
    loading.value = true;
    errorMessage.value = "";
    try {
        await axiosClient.put(`/users/me/updatePassword`, {
            currentPassword: (event.target as HTMLFormElement).currentPassword.value,
            newPassword: (event.target as HTMLFormElement).newPassword.value,
            confirmNewPassword: (event.target as HTMLFormElement).confirmNewPassword.value
        });
        successMessage.value = "Your password has been updated successfully.";
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while editing password. " + (error as AxiosError).message;
    } finally {
        loading.value = false;
    }
};

const submitHandler = computed(() =>
    (userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)) && userIdParam !== userId.value
        ? editUserPassword
        : editOwnPassword
);

</script>

<template>
    <div class="container py-5">
        <BackButton />
        <h1
            v-if="(userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)) && userIdParam !== userId">
            Change users Password
        </h1>
        <h1 v-else>Change Password</h1>

        <div v-if="loading" class="text-center">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden"></span>
            </div>
        </div>

        <div v-if="errorMessage" class="alert alert-danger text-center">
            {{ errorMessage }}
        </div>

        <div v-if="successMessage" class="alert alert-success text-center">
            {{ successMessage }}
        </div>

        <form @submit.prevent="submitHandler">
            <FormInput
                v-if="(!userStore.roles.includes(Role.EMPLOYEE) && !userStore.roles.includes(Role.ADMIN)) || userId === userIdParam"
                name="currentPassword" label="Current Password" type="password"
                placeholder="Enter your current password" />
            <FormInput name="newPassword" label="New Password" type="password" placeholder="Enter a new password" />
            <FormInput name="confirmNewPassword" label="Confirm New Password" type="password"
                placeholder="Re-enter your new password" />
            <button class="btn btn-primary w-100 py-2 my-3" type="submit">Change Password</button>
        </form>

    </div>

</template>
