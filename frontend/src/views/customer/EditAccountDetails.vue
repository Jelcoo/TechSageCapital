<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import { useUserStore } from "@/stores/user";
import axiosClient from "@/axios";
import type { User } from "@/types";
import { Role } from "@/types";
import type { AxiosError } from "axios";
import { useRoute } from "vue-router";
import { AccountStatus } from "@/types/user";
import 'bootstrap-icons/font/bootstrap-icons.css';
import { formatMoney } from "@/utils";
import BackButton from "@/components/BackButton.vue";

const route = useRoute();
const userIdParam = route.params.id;
const userStore = useUserStore();
const user = ref<User | null>(null);
const userId = ref(userStore.id);
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

const editUser = async (event: Event) => {
    event.preventDefault();
    loading.value = true;
    errorMessage.value = "";
    try {
        if (userStore.id === user.value?.id) {
            if (confirm("This will log you out. Do you want to continue?")) {
                await axiosClient.put(`/users/${userId.value}/update`, user.value);
                userStore.logout();
            }
        }
        else {
            await axiosClient.put(`/users/${userIdParam}/update`, user.value);
            successMessage.value = "User details updated successfully.";
        }
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while editing user details. " + (error as AxiosError).message;
    } finally {
        loading.value = false;
    }
};

const editSelf = async (event: Event) => {
    event.preventDefault();
    loading.value = true;
    errorMessage.value = "";
    try {
        if (confirm("This will log you out. Do you want to continue?")) {
            await axiosClient.put(`/users/me`, {
                currentEmail: userStore.email,
                email: user.value?.email,
                phoneNumber: user.value?.phoneNumber
            });
            userStore.logout();
        }
    } catch (error) {
        errorMessage.value = (error as AxiosError).response
            ? ((error as AxiosError).response?.data as { message?: string })?.message ?? "An unknown error occurred."
            : "An error occurred while editing user details. " + (error as AxiosError).message;
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    fetchUser();
});

const submitHandler = computed(() =>
    userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)
        ? editUser
        : editSelf
);
</script>

<template>
    <main>
        <div class="container py-5">
            <BackButton />
            <h1 class="display-4 fw-bold text-left mb-5">Edit account details</h1>

            <div v-if="loading" class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>

            <div v-if="errorMessage" class="alert alert-danger text-center">
                {{ errorMessage }}
            </div>

            <div v-if="successMessage" class="alert alert-success text-center">
                {{ successMessage }}
            </div>

            <h2>User Details</h2>
            <form @submit="submitHandler" class="container row mb-4">
                <div v-if="user" class="col-12 customer-details">
                    <div class="row mb-3">
                        <div class="col">
                            <strong>Firstname: </strong>
                            <input type="text"
                                v-if="userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)"
                                v-model="user.firstName" class="form-control" />
                            <span v-else>{{ user.firstName }}</span>
                        </div>
                        <div class="col">
                            <strong>Lastname: </strong>
                            <input type="text"
                                v-if="userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)"
                                v-model="user.lastName" class="form-control" />
                            <span v-else>{{ user.lastName }}</span>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col">
                            <strong>Email: </strong>
                            <input type="text" v-model="user.email" class="form-control" />
                        </div>
                        <div class="col">
                            <strong>Phone Number: </strong>
                            <input type="text" v-model="user.phoneNumber" class="form-control" />
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col">
                            <strong>BSN: </strong>
                            <input type="text"
                                v-if="userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)"
                                v-model="user.bsn" class="form-control" />
                            <span v-else>{{ user.bsn }}</span>
                        </div>
                        <div class="col">
                            <strong>Status: </strong>
                            <select
                                v-if="userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)"
                                v-model="user.status" class="form-control">
                                <option v-for="status in Object.values(AccountStatus)" :key="status" :value="status">
                                    {{ status }}
                                </option>
                            </select>
                            <span v-else>{{ user.status }}</span>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col">
                            <strong>Daily Limit: </strong>
                            <input type="text"
                                v-if="userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)"
                                v-model="user.dailyLimit" class="form-control" />
                            <span v-else> {{ formatMoney(user.dailyLimit) }}</span>

                        </div>
                        <div class="col">
                            <strong>Transfer Limit: </strong>
                            <input type="text"
                                v-if="userStore.roles.includes(Role.EMPLOYEE) || userStore.roles.includes(Role.ADMIN)"
                                v-model="user.transferLimit" class="form-control" />
                            <span v-else> {{ formatMoney(user.transferLimit) }}</span>

                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col">
                            <button type="submit" class="btn btn-primary">Confirm</button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </main>
</template>
