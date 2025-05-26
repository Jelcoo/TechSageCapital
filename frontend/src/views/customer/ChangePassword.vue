<script setup lang="ts">
import axiosClient from "@/axios";
import { onMounted, ref } from "vue";
import FormInput from '@/components/forms/FormInput.vue';
import { useUserStore } from '@/stores/user';
import type { AxiosError } from 'axios';
import { Role } from "@/types";
import { useRoute } from "vue-router";
import type { User } from "@/types";

const route = useRoute();
const userIdParam = route.params.id;
const userStore = useUserStore();
const user = ref<User | null>(null);
const errorMessage = ref("");
const successMessage = ref("");
const loading = ref(false);

async function fetchUser() {
    loading.value = true;
    errorMessage.value = "";
    try {
        const response = await axiosClient.get<User>(`/users/${userIdParam ?? 'me'}`);
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

function onSubmit() {
    const form = document.querySelector('form');
    if (!form) return;

    const currentPassword = (form.elements.namedItem('currentPassword') as HTMLInputElement).value;
    const newPassword = (form.elements.namedItem('newPassword') as HTMLInputElement).value;
    const confirmNewPassword = (form.elements.namedItem('confirmNewPassword') as HTMLInputElement).value;

    if (newPassword !== confirmNewPassword) {
        alert("New passwords do not match.");
        return;
    }
    // remove this after testing
    console.log("Current Password:", currentPassword);
    console.log("New Password:", newPassword);

    // implement logic here to differentiate between user and employee
    // this will be employee logic, user logic will have more back-end validation
    try {
        if (!userStore.roles.includes(Role.EMPLOYEE)) {
            axiosClient.put('/users/changePassword', {
                currentPassword,
                newPassword
            })
        }

    } catch (error) {
        console.error("Error changing password:", error);
        alert("An error occurred while changing the password. Please try again.");
        return;
    }
    finally {
        alert("Password changed successfully.");
    }

    form.reset();
}
onMounted(() => {
    if (userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)) {
        fetchUser();
    }
});


</script>

<template>
    <div>
        <h1>Change Password</h1>

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

        <form @submit.prevent="onSubmit">
            <FormInput name="currentPassword" label="Current Password" type="password"
                placeholder="Enter your current password" />
            <FormInput name="newPassword" label="New Password" type="password" placeholder="Enter a new password" />
            <FormInput name="confirmNewPassword" label="Confirm New Password" type="password"
                placeholder="Re-enter your new password" />
            <button class="btn btn-primary w-100 py-2 my-3" type="submit">Change Password</button>
        </form>

    </div>

</template>
