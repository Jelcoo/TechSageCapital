import { defineStore } from 'pinia';
import axios from '@/axios';
import type { AxiosResponse } from 'axios';
import { AccountStatus, Role, type User } from '@/types';

interface StoreUser extends User {
    token: string | null;
}

export const useUserStore = defineStore('user', {
    state: (): StoreUser => ({
        id: 0,
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        bsn: '',
        role: Role.CUSTOMER,
        dailyLimit: 0,
        transferLimit: 0,
        createdAt: new Date(),
        status: AccountStatus.ACTIVE,
        token: localStorage.getItem('token') || null,
    }),
    getters: {
        fullName: (state) => `${state.firstName} ${state.lastName}`,
        isAuthenticated: (state) => !!state.token,
    },
    actions: {
        me(): Promise<AxiosResponse<{ user: User }>> {
            return new Promise((resolve, reject) => {
                axios
                    .get('/me')
                    .then((res) => {
                        resolve(res);
                    })
                    .catch((error) => reject(error));
            });
        },
        login(email: string, password: string, turnstileToken: string) {
            return new Promise((resolve, reject) => {
                axios
                    .post('/auth/login', {
                        email,
                        password,
                        'cf-turnstile-response': turnstileToken,
                    })
                    .then((res) => {
                        this.resetStores();
                        this.setUserResponse(res);
                        this.token = res.data.token;
                        localStorage.setItem('token', res.data.token);
                        axios.defaults.headers.common['Authorization'] = 'Bearer ' + this.token;
                        resolve(res);
                    })
                    .catch((error) => reject(error));
            });
        },
        autoLogin() {
            if (!this.token) {
                return;
            }

            axios.defaults.headers.common['Authorization'] = 'Bearer ' + this.token;

            return new Promise((resolve, reject) => {
                axios
                    .get('/me')
                    .then((res) => {
                        this.resetStores();
                        this.setUserResponse(res);
                        resolve(res);
                    })
                    .catch((error) => {
                        if (error.response.status === 401) {
                            this.logout();
                        }
                        reject(error);
                    });
            });
        },
        setUserResponse(res: AxiosResponse) {
            this.id = res.data.user.id;
            this.firstName = res.data.user.firstName;
            this.lastName = res.data.user.lastName;
            this.email = res.data.user.email;
            this.phoneNumber = res.data.user.phoneNumber;
            this.bsn = res.data.user.bsn;
            this.role = res.data.user.role;
            this.dailyLimit = res.data.user.dailyLimit;
            this.transferLimit = res.data.user.transferLimit;
            this.createdAt = res.data.user.createdAt;
            this.status = res.data.user.status;
        },
        logout() {
            localStorage.removeItem('token');
            axios.defaults.headers.common['Authorization'] = '';
            this.resetStores();
        },
        resetStores() {
            this.$reset();
        },
    },
});
