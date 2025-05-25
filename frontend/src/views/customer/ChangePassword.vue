<script setup lang="ts">
import axios from "@/axios";
import { ref } from "vue";

const errorMessage = ref("");
const loading = ref(false);

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
        axios.put('/users/changePassword', {
            currentPassword,
            newPassword
        })
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

        <form @submit.prevent="onSubmit">
            <FormInput name="currentPassword" label="Current Password" type="password" placeholder="Current Password" />
            <FormInput name="newPassword" label="New Password" type="password" placeholder="New Password" />
            <FormInput name="confirmNewPassword" label="Confirm New Password" type="password"
                placeholder="Confirm New Password" />
            <button class="btn btn-primary w-100 py-2 my-3" type="submit">Change Password</button>
        </form>
    </div>

</template>
