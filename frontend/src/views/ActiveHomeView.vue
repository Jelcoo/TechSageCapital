<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { formatMoney } from "@/utils";
import { faMoneyBillTransfer, faPerson } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";

const userStore = useUserStore();
</script>

<template>
    <main>
        <div class="container py-5 gap-3 d-flex flex-column">
            <h1 class="display-4 fw-bold text-left">Welcome {{ userStore.firstName }}</h1>

            <div class="d-flex gap-2 flex-wrap">
                <button class="btn btn-primary">
                    <RouterLink class="text-white text-decoration-none" to="/transfer">
                        <FontAwesomeIcon :icon="faMoneyBillTransfer" class="me-2" /> Transfer
                    </RouterLink>
                </button>
                <button class="btn btn-secondary">
                    <RouterLink class="text-white text-decoration-none" to="/accountdetails">
                        <FontAwesomeIcon :icon="faPerson" class="me-2" /> Account Details
                    </RouterLink>
                </button>
            </div>

            <div v-if="userStore.bankAccounts.length > 0" class="bank-accounts">
                <ul class="list-group">
                    <li class="list-group-item d-flex justify-content-between" v-for="account in userStore.bankAccounts"
                        :key="account.id">
                        <div>
                            <div>
                                <strong>Account Type:</strong> {{ account.type }}
                            </div>
                            <div>
                                <strong>IBAN:</strong> {{ account.iban }}
                            </div>
                            <div>
                                <strong>Account Balance:</strong> {{ formatMoney(account.balance) }}
                            </div>
                        </div>

                        <div class="ml-auto">
                            <RouterLink :to="`/accountdetails/transactions/${account.id}/${account.iban}`"
                                class="btn btn-primary">View
                                Transactions</RouterLink>
                        </div>
                    </li>
                </ul>
            </div>
            <div v-else class="alert alert-info">
                No bank accounts found for this user.
            </div>
        </div>
    </main>
</template>
