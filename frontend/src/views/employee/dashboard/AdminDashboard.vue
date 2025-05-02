<script setup lang="ts">
import { RouterLink } from "vue-router";
</script>

<template>
    <main>
        <h1>Administration dashboard</h1>
        <aside>
            <nav>
                <ul>
                    <li>
                        <RouterLink to="/employee/Customers">Customers</RouterLink>
                        <!-- <RouterLink to="/admin/employees">Employees</RouterLink>    make sure this only get's shown if the user is an admin -->
                    </li>
                </ul>
            </nav>
        </aside>
    </main>
</template>
