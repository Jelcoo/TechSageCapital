<script setup lang="ts">
import { RouterLink, } from 'vue-router';
import { useUserStore } from '@/stores/user';
const userStore = useUserStore();
</script>

<template>
    <main>
        <div class="position-relative overflow-hidden p-3 p-md-5 m-md-3 text-center bg-light text-dark"
            style="width: 100%;">
            <div class="col-md-5 p-lg-5 mx-auto my-5">
                <h1 class="display-4 font-weight-normal">TechSage Capital</h1>
                <p class="lead font-weight-normal">Modern Banking. Timeless Trust.</p>
                <RouterLink to="/auth/register" class="btn btn-outline-secondary" active-class="active"
                    v-if="userStore.token == null">Sign-up
                </RouterLink>
            </div>
            <div class="product-device box-shadow d-none d-md-block"></div>
            <div class="product-device product-device-2 box-shadow d-none d-md-block"></div>
        </div>
        <div class="d-md-flex flex-md-equal w-100 my-md-3 m-md-3 pl-md-3">
            <div class="bg-dark mr-md-3 pt-3 px-3 pt-md-5 px-md-5 text-center text-white overflow-hidden">
                <div class="my-3 py-3">
                    <h2 class="display-5">Innovation you can rely on</h2>
                    <p class="lead">We combine the latest in fintech with a commitment to long-term relationships, so
                        you get a banking experience that’s fast, intuitive, and built on trust.</p>
                </div>
                <div class="bg-light box-shadow mx-auto"
                    style="width: 80%; height: 300px; border-radius: 21px 21px 0 0;"></div>
            </div>
            <div class="bg-light mr-md-3 pt-3 px-3 pt-md-5 px-md-5 text-center overflow-hidden text-dark">
                <div class="my-3 p-3">
                    <h2 class="display-5">Tech-Powered. Human-Centered.</h2>
                    <p class="lead">Our intuitive technology is designed to serve you—with guidance from real financial
                        experts when you need it.</p>
                </div>
                <div class="bg-dark box-shadow mx-auto"
                    style="width: 80%; height: 300px; border-radius: 21px 21px 0 0;"></div>
            </div>
        </div>
    </main>
</template>
