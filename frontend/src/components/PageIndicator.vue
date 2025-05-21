<template>
    <slot />
    <div class="d-flex justify-content-between align-items-center my-2">
        <p class="text-muted small mb-0">
            Showing&nbsp;
            <span class="fw-semibold">
                {{ Math.max(start, Math.min(props.pagination.totalRecords, 1)) }}
            </span>
            &nbsp;to&nbsp;
            <span class="fw-semibold">{{ end }}</span> of&nbsp;
            <span class="fw-semibold">{{ props.pagination.totalRecords }}</span> results.
        </p>

        <div v-if="pagination.totalPages > 1" class="btn-group">
            <button class="btn btn-sm btn-outline-secondary" @click="emit('pageSelect', 1)"
                :disabled="pagination.previousPages.length !== 2" title="First Page">
                <FontAwesomeIcon :icon="faAnglesLeft" />
            </button>

            <button v-for="value in pagination.previousPages" :key="`previous-${value}`"
                class="btn btn-sm btn-outline-secondary" @click="emit('pageSelect', value)">
                {{ value }}
            </button>

            <button class="btn btn-sm btn-primary" disabled>
                {{ pagination.currentPage }}
            </button>

            <button v-for="value in pagination.nextPages" :key="`next-${value}`"
                class="btn btn-sm btn-outline-secondary" @click="emit('pageSelect', value)">
                {{ value }}
            </button>

            <button class="btn btn-sm btn-outline-secondary" @click="emit('pageSelect', pagination.totalPages)"
                :disabled="pagination.nextPages.length !== 2" title="Last Page">
                <FontAwesomeIcon :icon="faAnglesRight" />
            </button>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { PaginatedResponse } from '@/axios';
import { faAnglesLeft, faAnglesRight } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { computed } from 'vue';

const props = defineProps<{
    pagination: PaginatedResponse<unknown>;
}>();
const emit = defineEmits(['pageSelect']);

const start = computed(() => {
    return (props.pagination.currentPage - 1) * props.pagination.recordsPerPage + 1;
});
const end = computed(() => {
    return start.value + props.pagination.recordsThisPage - 1;
});
</script>

<style scoped>
.btn-primary:disabled {
    opacity: 1 !important;
}
</style>
