<script setup lang="ts">
import StatusPage from '@/components/StatusPage.vue';
</script>

<template>
    <StatusPage title="Pending"
        :message="'Your account is still being processed. Please wait a few minutes and try again later.'"
        :home-visible="false" />
</template>
