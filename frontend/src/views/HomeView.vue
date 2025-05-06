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
        </div>
        <div class="d-md-flex flex-md-equal w-100 my-md-3 m-md-3 pl-md-3 gap-3 row">
            <div class="bg-dark pt-3 px-3 pt-md-5 px-md-5 text-center text-white overflow-hidden col">
                <div class="my-3 py-3">
                    <h2 class="display-5">Innovation you can rely on</h2>
                    <p class="lead">We combine the latest in fintech with a commitment to long-term relationships, so
                        you get a banking experience that’s fast, intuitive, and built on trust.</p>
                </div>
            </div>
            <div class="bg-light pt-3 px-3 pt-md-5 px-md-5 text-center overflow-hidden text-dark col">
                <div class="my-3 p-3">
                    <h2 class="display-5">Tech-Powered. Human-Centered.</h2>
                    <p class="lead">Our intuitive technology is designed to serve you—with guidance from real financial
                        experts when you need it.</p>
                </div>
            </div>
        </div>
        <div class="d-md-flex flex-md-equal w-100 my-md-3 m-md-3 pl-md-3 gap-3 row">
            <div class="bg-light pt-3 px-3 pt-md-5 px-md-5 text-center overflow-hidden text-dark col">
                <div class="my-3 p-3">
                    <h2 class="display-5">Always On. Always Secure.</h2>
                    <p class="lead">Access your finances anytime, anywhere—with enterprise-grade security protecting
                        every move.</p>
                </div>
            </div>
            <div class="bg-dark pt-3 px-3 pt-md-5 px-md-5 text-center text-white overflow-hidden col">
                <div class="my-3 py-3">
                    <h2 class="display-5">Clarity in Every Click</h2>
                    <p class="lead">Say goodbye to hidden fees and confusing interfaces. Say hello to clear, honest
                        banking.</p>
                </div>
            </div>
        </div>
    </main>
</template>
