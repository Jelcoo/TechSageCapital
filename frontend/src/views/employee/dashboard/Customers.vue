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
        const response = await axios.get("http://localhost/users/getAll");
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

async function softDeleteCustomer(customerId: number) {
    if (confirm("Are you sure you want to delete this customer?")) {
        try {
            await axios.patch(`http://localhost/users/delete/${customerId}`); //patch because soft delete
            fetchCustomers();
        } catch (error) {
            const err = error as AxiosError;
            errorMessage.value = err.response
                ? (err.response.data as { message: string }).message
                : "An error occurred while deleting the customer. " + err.message; //again debugging line (see line 22)
        }
    }

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
                            <tr v-for="(customers, index) in customers" :key="customers.id">
                                <td>{{ customers.FirstName }}</td>
                                <td>{{ customers.LastName }}</td>
                                <td>{{ customers.email }}</td>
                                <td>{{ customers.PhoneNumber }}</td>
                                <td>{{ customers.BSN }}</td>
                                <td>{{ customers.DailyLimit }}</td>
                                <td>{{ customers.TransferLimit }}</td>
                                <td>{{ customers.Status }}</td>
                                <td>
                                    <button class="btn btn-primary btn-sm me-2" @click="editCustomer(customers.id)">
                                        Edit
                                    </button>
                                    <button class="btn btn-danger btn-sm" @click="softDeleteCustomer(customers.id)">
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