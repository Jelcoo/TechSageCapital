<script setup lang="ts">
import { ref, onMounted } from "vue";
import type { User } from "../../../../types/User";
import axios, { AxiosError } from "axios";

const customers = ref<User[]>([]);
const errorMessage = ref("");
const loading = ref(false);


async function fetchCustomers() {
    loading.value = true;
    errorMessage.value = "";

    try {
        const response = await axios.get("http://localhost/users");
        customers.value = response.data;
    } catch (error) {
        const err = error as AxiosError;
        errorMessage.value = err.response
            ? (err.response.data as { message: string }).message
            : "An error occurred while fetching customers. " + err.message; //remove err.message later (is just for debugging :))
    } finally {
        loading.value = false;
    }
}


function editCustomer(customerId: number) {
    // Logic to edit customer
    console.log("Edit customer with ID:", customerId);
}

function deleteCustomer(customerId: number) {
    // Logic to delete customer
    console.log("Delete customer with ID:", customerId);
}

onMounted(() => {
    fetchCustomers();
});
</script>

<template>
    <main>
        <div class="container py-5">
            <h1 class="display-4 fw-bold text-center mb-4">Customers</h1>

            <div v-if="loading" class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>

            <div v-if="errorMessage" class="alert alert-danger text-center">
                {{ errorMessage }}
            </div>

            <div v-if="!loading && !errorMessage">
                <div v-if="customers.length === 0" class="text-center">
                    <p class="lead">No Customers found.</p>
                </div>
                <div v-else>
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Firstname</th>
                                <th>Lastname</th>
                                <th>Email</th>
                                <th>Phone Number</th>
                                <th>BSN</th>
                                <th>Daily limit</th>
                                <th>Transfer limit</th>
                                <th>status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(customers, index) in customers" :key="customers.ID">
                                <td>{{ customers.Firstname }}</td>
                                <td>{{ customers.Lastname }}</td>
                                <td>{{ customers.Email }}</td>
                                <td>{{ customers.PhoneNumber }}</td>
                                <td>{{ customers.BSN }}</td>
                                <td>{{ customers.DailyLimit }}</td>
                                <td>{{ customers.TransferLimit }}</td>
                                <td>{{ customers.Status }}</td>
                                <td>
                                    <button class="btn btn-primary btn-sm me-2" @click="editCustomer(customers.ID)">
                                        Edit
                                    </button>
                                    <button class="btn btn-danger btn-sm" @click="deleteCustomer(customers.ID)">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</template>