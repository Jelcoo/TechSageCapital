<script setup lang="ts">
import { RouterView } from 'vue-router';
import NavbarComponent from '@/components/NavbarComponent.vue';
import FooterComponent from '@/components/FooterComponent.vue';
import { AuthenticationScope, useUserStore } from '@/stores/user';

const userStore = useUserStore();
</script>

<template>
    <div class="page">
        <NavbarComponent v-if="userStore.authenticationScope !== AuthenticationScope.ATM" />

        <div class="container mainbody">
            <RouterView />
        </div>

        <FooterComponent v-if="userStore.authenticationScope !== AuthenticationScope.ATM" />
    </div>
</template>
