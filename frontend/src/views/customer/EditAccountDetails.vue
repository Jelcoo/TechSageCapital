<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useUserStore } from "@/stores/user";
import axiosClient from "@/axios";
import type { User } from "@/types";
import { Role } from "@/types";
import type { AxiosError } from "axios";
import { useRoute } from "vue-router";

const userStore = useUserStore();
const user = ref<User | null>(null);
const userId = ref(userStore.id);
const errorMessage = ref("");
const loading = ref(false);

async function fetchUser() {
    loading.value = true;
    errorMessage.value = "";
    try {
        if (useRoute().params.id) {
            userId.value = Number(useRoute().params.id);
        }
        const response = await axiosClient.get<User>(`/users/${userId.value}`);
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

async function editAccount() {
    loading.value = true;
    errorMessage.value = "";

}

onMounted(() => {
    fetchUser();
});
</script>

<template>
    <main>
        <div class="container py-5">
            <h1 class="display-4 fw-bold text-left mb-5">Edit account details</h1>

            <div v-if="loading" class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>

            <div v-if="errorMessage" class="alert alert-danger text-center">
                {{ errorMessage }}
            </div>
            <h2>User Details</h2>
            <div class="container row mb-4">
                <div v-if="user" class="col-12 customer-details">
                    <form class="row mb-3">
                        <div class="col">
                            <Strong>Firstname:</strong>
                            <input type="text" class="form-control" v-model="user.firstName" />
                        </div>
                        <div class="col">
                            <strong>Lastname:</strong>
                            <input type="text" class="form-control" v-model="user.lastName" />
                        </div>
                    </form>

                    <div class="row mb-3">
                        <div class="col">
                            <strong>Email:</strong> {{ user.email }}
                        </div>
                        <div class="col">
                            <strong>Phone Number:</strong> {{ user.phoneNumber }}
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col">
                            <strong>BSN:</strong> {{ user.bsn }}
                        </div>
                        <div class="col">
                            <strong>Status:</strong> {{ user.status }}
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col">
                            <strong>Daily Limit:</strong> €{{ user.dailyLimit }}
                        </div>
                        <div class="col">
                            <strong>Transfer Limit:</strong> €{{ user.transferLimit }}
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col">
                            <button class="btn btn-primary" @click="editAccount">Confirm</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</template>
