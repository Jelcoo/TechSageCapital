<script setup lang="ts">
import { ref, onMounted } from "vue";
import type { User } from "../../../types/User";

const customer = ref<User[]>([]);  //make this a stored customer object
const errorMessage = ref("");
const loading = ref(false);



function editCustomer(customerId: number) {
    // Logic to edit customer
    console.log("Edit customer with ID:", customerId);
}

function deleteCustomer(customerId: number) {
    // Logic to delete customer
    console.log("Delete customer with ID:", customerId);
}
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
                    <td>{{ customer.Firstname }}</td>
                    <td>{{ customer.Lastname }}</td>
                    <td>{{ customer.Email }}</td>
                    <td>{{ customer.PhoneNumber }}</td>
                    <td>{{ customer.BSN }}</td>
                    <td>{{ customer.DailyLimit }}</td>
                    <td>{{ customer.TransferLimit }}</td>
                    <td>{{ customer.Status }}</td>
                    <td>
                        <button class="btn btn-primary btn-sm me-2" @click="editCustomer(customers.ID)">
                            Edit
                        </button>
                        <button class="btn btn-danger btn-sm" @click="deleteCustomer(customers.ID)">
                            Delete
                        </button>
                    </td>
                </tbody>
            </table>
        </div>
    </main>
</template>