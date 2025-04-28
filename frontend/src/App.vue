<script setup lang="ts">
import { RouterView } from 'vue-router';
import NavbarComponent from '@/components/NavbarComponent.vue';
import FooterComponent from '@/components/FooterComponent.vue';
import { onBeforeMount } from 'vue';
import { useUserStore } from './stores/user';

onBeforeMount(() => {
    const userStore = useUserStore();
    userStore.autoLogin();
});
</script>

<template>
    <NavbarComponent />

    <div class="container">
        <RouterView />
    </div>

    <FooterComponent />
</template>
