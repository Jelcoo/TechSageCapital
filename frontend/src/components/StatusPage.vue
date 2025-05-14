<script setup lang="ts">
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { faHouse } from '@fortawesome/free-solid-svg-icons';

const props = defineProps<{
    title: string;
    message: string;
}>();
</script>

<template>
    <div class="text-center mt-5">
        <h1 class="display-1 fw-bold">{{ props.title }}</h1>
        <p class="fs-4 fw-semibold text-secondary mt-3">{{ props.message }}</p>
        <RouterLink to="/" class="btn btn-primary mt-4">
            <FontAwesomeIcon :icon="faHouse" class="me-2" />
            Go back to the homepage
        </RouterLink>
    </div>
</template>
