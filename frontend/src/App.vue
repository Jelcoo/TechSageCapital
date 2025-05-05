<script setup lang="ts">
import { RouterView } from 'vue-router';
import NavbarComponent from '@/components/NavbarComponent.vue';
import FooterComponent from '@/components/FooterComponent.vue';
import { onBeforeMount, ref } from 'vue';
import { useUserStore } from './stores/user';

const ready = ref(false);

onBeforeMount(() => {
    const userStore = useUserStore();
    userStore.autoLogin().then(() => {
        ready.value = true;
    }).catch(() => {
        ready.value = false;
    });

});
</script>

<template>
    <NavbarComponent />

    <div class="container" v-if="ready">
        <RouterView />
    </div>

    <FooterComponent />
</template>
