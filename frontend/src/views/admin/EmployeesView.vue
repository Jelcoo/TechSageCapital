<script setup lang="ts">
import { ref, onMounted } from "vue";
import type { User } from "@/types";

const employees = ref<User[]>([]);
const errorMessage = ref("");
const loading = ref(false);


function fetchEmployees() {

}


function editEmployee(employeeId: number) {
    // Logic to edit an employee
    // Maybe make this a modal or a new page, new page is easier, modal looks better
    console.log("Edit employee with ID:", employeeId);
}

function deleteEmployee(employeeId: number) {
    // Add logic to delete an employee
    console.log("Delete employee with ID:", employeeId);
}

onMounted(() => {
    fetchEmployees();
});
</script>

<template>
    <main>
        <div class="container py-5">
            <h1 class="display-4 fw-bold text-center mb-4">Employees</h1>

            <div v-if="loading" class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>

            <div v-if="errorMessage" class="alert alert-danger text-center">
                {{ errorMessage }}
            </div>

            <div v-if="!loading && !errorMessage">
                <div v-if="employees.length === 0" class="text-center">
                    <p class="lead">No Employees found.</p>
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
                            <tr v-for="employees in employees" :key="employees.id">
                                <td>{{ employees.firstName }}</td>
                                <td>{{ employees.lastName }}</td>
                                <td>{{ employees.email }}</td>
                                <td>{{ employees.phoneNumber }}</td>
                                <td>{{ employees.bsn }}</td>
                                <td>{{ employees.dailyLimit }}</td>
                                <td>{{ employees.transferLimit }}</td>
                                <td>{{ employees.status }}</td>
                                <td>
                                    <button class="btn btn-primary btn-sm me-2" @click="editEmployee(employees.id)">
                                        Edit
                                    </button>
                                    <button class="btn btn-danger btn-sm" @click="deleteEmployee(employees.id)">
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