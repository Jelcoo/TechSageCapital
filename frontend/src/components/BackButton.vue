<script setup lang="ts">
import { useRouter } from "vue-router";
import 'bootstrap-icons/font/bootstrap-icons.css';

const router = useRouter();

function returnToPreviousPage() {
    router.back();
}
</script>

<template>
    <button class="btn btn-secondary mb-3" @click="returnToPreviousPage">
        <i class="bi bi-arrow-left"></i> Back
    </button>
</template>
