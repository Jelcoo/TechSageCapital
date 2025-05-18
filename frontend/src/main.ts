import './assets/main.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap';

import { createApp } from 'vue';
import { createPinia } from 'pinia';

import App from './App.vue';
import router from './router';
import { useUserStore } from './stores/user';

const app = createApp(App);

app.use(createPinia());

const userStore = useUserStore();

userStore.autoLogin().finally(() => {
    app.use(router);

    app.mount('#app');
});
