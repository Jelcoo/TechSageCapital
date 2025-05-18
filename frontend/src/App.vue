<script setup lang="ts">
import { RouterView } from 'vue-router';
import NavbarComponent from '@/components/NavbarComponent.vue';
import FooterComponent from '@/components/FooterComponent.vue';
</script>

<template>
    <div class="page">
        <NavbarComponent />

        <div class="container mainbody">
            <RouterView />
        </div>

        <FooterComponent />
    </div>
</template>
