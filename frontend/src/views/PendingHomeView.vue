<script setup lang="ts">
import StatusPage from '@/components/StatusPage.vue';
</script>

<template>
    <StatusPage title="Pending" :message="'Your account is still being processed. Please wait until it is approved.'"
        :home-visible="false" />
</template>
