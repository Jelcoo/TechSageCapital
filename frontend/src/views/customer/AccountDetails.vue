<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useUserStore } from "@/stores/user";
import axios from "axios";
import type { User } from "../../../types/User";

//const customer = useUserStore();
const customer = ref<User | null>(null);
const errorMessage = ref("");
const loading = ref(false);

async function fetchUser() {
    loading.value = true;
    errorMessage.value = "";

    try {
        const userId = 1; // Replace with the actual ID from the store
        const response = await axios.get<User>(`http://localhost:8080/users/getById/${userId}`);
        customer.value = response.data;
        if (!customer.value || customer.value == null) {
            errorMessage.value = "User not found.";
        }
    } catch (error) {
        errorMessage.value = "An error occurred while fetching user details.";
    } finally {
        loading.value = false;
    }
}

function editAccount() {   //idk if this is necessary. Should a customer be able to edit their own account or can it only be done by an admin / employee?
    // Logic to edit customer    
    console.log("Edit customer with ID:", customer.id);
}

//instead of a stored user, should we get an updated customer from the database incase an employee has edited something?

onMounted(() => {
    fetchUser();
});
</script>

<template>
    <main>
        <div class="container py-5">
            <h1 class="display-4 fw-bold text-center mb-4">Account details</h1>

            <div v-if="loading" class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>

            <div v-if="errorMessage" class="alert alert-danger text-center">
                {{ errorMessage }}
            </div>

            <div v-if="customer" class="customer-details">
                <div class="mb-3">
                    <strong>Firstname:</strong> {{ customer.firstName }}
                </div>
                <div class="mb-3">
                    <strong>Lastname:</strong> {{ customer.lastName }}
                </div>
                <div class="mb-3">
                    <strong>Email:</strong> {{ customer.email }}
                </div>
                <div class="mb-3">
                    <strong>Phone Number:</strong> {{ customer.phoneNumber }}
                </div>
                <div class="mb-3">
                    <strong>BSN:</strong> {{ customer.bsn }}
                </div>
                <div class="mb-3">
                    <strong>Daily Limit:</strong> €{{ customer.dailyLimit }}
                </div>
                <div class="mb-3">
                    <strong>Transfer Limit:</strong> €{{ customer.transferLimit }}
                </div>
                <div class="mb-3">
                    <strong>Status:</strong> {{ customer.status }}
                </div>
                <!-- Uncomment if actions are needed -->
                <!--
                <div class="mt-4">
                    <button class="btn btn-primary me-2" @click="editAccount()">
                        Edit
                    </button>
                    <button class="btn btn-danger" @click="deleteAccount()">
                        Delete
                    </button>
                </div>
                -->
            </div>
        </div>
    </main>
</template>