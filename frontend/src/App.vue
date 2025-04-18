<script setup lang="ts">
import { RouterView } from 'vue-router';
import NavbarComponent from '@/components/NavbarComponent.vue';
import FooterComponent from '@/components/FooterComponent.vue';
</script>

<template>
    <NavbarComponent />

    <RouterView />

    <FooterComponent />
</template>
