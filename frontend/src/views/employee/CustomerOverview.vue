<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { AccountStatus, type User } from '@/types';
import axiosClient, { type PaginatedResponse } from '@/axios';
import type { AxiosError } from 'axios';
import PageIndicator from '@/components/PageIndicator.vue';

const customers = ref<PaginatedResponse<User>>();
const errorMessage = ref('');
const loading = ref(false);
const searchQuery = ref('ACTIVE');
const page = ref(1);

function fetchCustomers() {
    axiosClient.get<PaginatedResponse<User>>(`/users?status=${searchQuery.value}&page=${page.value}`).then(({ data }) => {
        customers.value = data;
    });
}

async function softDeleteCustomer(customerId: number) {
    if (confirm("Are you sure you want to delete this customer?")) {
        try {
            await axiosClient.delete(`/users/${customerId}/softDelete`);
            fetchCustomers();
        } catch (error) {
            const err = error as AxiosError;
            errorMessage.value = err.response
                ? (err.response.data as { message: string }).message
                : "An error occurred while deleting the customer. " + err.message; // remove err.message later is a debugging line
        }
    }
}

async function reinstate(customerId: number) {
    if (confirm("Are you sure you want to reinstate this customer?")) {
        try {
            await axiosClient.put(`/users/${customerId}/reinstate`);
            fetchCustomers();
        } catch (error) {
            const err = error as AxiosError;
            errorMessage.value = err.response
                ? (err.response.data as { message: string }).message
                : "An error occurred while reinstating the customer. " + err.message; // remove err.message later is a debugging line
        }
    }
}

function handlePageSelect(pageNumber: number) {
    page.value = pageNumber;
    fetchCustomers();
}

onMounted(() => {
    fetchCustomers();
});
</script>

<template>
    <main>
        <div class="container py-5">
            <h1 class="display-4 fw-bold text-center mb-4">Customers</h1>

            <div v-if="loading || !customers" class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>

            <div v-if="errorMessage" class="alert alert-danger text-center">
                {{ errorMessage }}
            </div>

            <div class="mb-3">
                <label for="status" class="form-label">Account Status:</label>
                <select class="form-select" id="status" v-model="searchQuery" @change="fetchCustomers">
                    <option value="ACTIVE" selected>Active</option>
                    <option value="PENDING">Pending</option>
                    <option value="DELETED">Deleted</option>
                </select>
            </div>
            <div v-if="!customers || customers?.content.length === 0" class="text-center">
                <p class="lead">No Customers found.</p>
            </div>
            <PageIndicator v-else :pagination="customers" @pageSelect="handlePageSelect">
                <div class="table-responsive">
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
                            <tr v-for="customer in customers.content" :key="customer.id">
                                <td>{{ customer.firstName }}</td>
                                <td>{{ customer.lastName }}</td>
                                <td>{{ customer.email }}</td>
                                <td>{{ customer.phoneNumber }}</td>
                                <td>{{ customer.bsn }}</td>
                                <td>{{ customer.dailyLimit }}</td>
                                <td>{{ customer.transferLimit }}</td>
                                <td>{{ customer.status }}</td>
                                <td>
                                    <div v-if="customer.status === AccountStatus.ACTIVE" class="d-flex gap-2">
                                        <button class="btn btn-primary">
                                            <RouterLink :to="`/accountdetails/${customer.id}`"
                                                class="text-white text-decoration-none">Details</RouterLink>
                                        </button>
                                        <button class="btn btn-danger"
                                            @click="softDeleteCustomer(customer.id)">Delete</button>
                                    </div>
                                    <div v-else-if="customer.status === AccountStatus.PENDING" class="d-flex gap-2">
                                        <button class="btn btn-primary">
                                            <RouterLink :to="`/employee/customer/${customer.id}/approve`"
                                                class="text-white text-decoration-none">Approve</RouterLink>
                                        </button>
                                        <button class="btn btn-danger"
                                            @click="softDeleteCustomer(customer.id)">Reject</button>
                                    </div>
                                    <div v-else-if="customer.status === AccountStatus.DELETED" class="d-flex gap-2">
                                        <button class="btn btn-primary"
                                            @click="reinstate(customer.id)">Reinstate</button>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </PageIndicator>
        </div>
    </main>
</template>
