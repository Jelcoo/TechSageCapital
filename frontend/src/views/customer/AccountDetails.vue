<script setup lang="ts">
import { ref } from "vue";
import { useUserStore } from "@/stores/user";

const customer = useUserStore();
const errorMessage = ref("");
const loading = ref(false);



function editAccount() {   //idk if this is necessary. Should a customer be able to edit their own account or can it only be done by an admin / employee?
    // Logic to edit customer    
    console.log("Edit customer with ID:", customer.id);
}

//instead of a stored user, should we get an updated customer from the database incase an employee has edited something?
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
                    <td>{{ customer.firstName }}</td>
                    <td>{{ customer.lastName }}</td>
                    <td>{{ customer.email }}</td>
                    <td>{{ customer.phoneNumber }}</td>
                    <td>{{ customer.bsn }}</td>
                    <td>{{ customer.dailyLimit }}</td>
                    <td>{{ customer.transferLimit }}</td>
                    <td>{{ customer.status }}</td>
                    <!-- <td>
                         <button class="btn btn-primary btn-sm me-2" @click="editAccount()">
                            Edit
                        </button>
                        <button class="btn btn-danger btn-sm" @click="deleteAccount()">
                            Delete
                        </button>
                    </td> --> <!-- uncomment if customer can change their account -->
                </tbody>
            </table>
        </div>
    </main>
</template>