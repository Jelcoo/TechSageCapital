import { defineStore } from 'pinia';
import axiosClient from '@/axios';
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
        roles: [Role.CUSTOMER],
        dailyLimit: 0,
        transferLimit: 0,
        createdAt: new Date(),
        status: AccountStatus.ACTIVE,
        token: localStorage.getItem('token') || null,
    }),
    getters: {
        fullName: (state) => `${state.firstName} ${state.lastName}`,
        isAuthenticated: (state) => state.token !== null,
    },
    actions: {
        me(): Promise<AxiosResponse<{ user: User }>> {
            return new Promise((resolve, reject) => {
                axiosClient
                    .get('/users/me')
                    .then((res) => {
                        resolve(res);
                    })
                    .catch((error) => reject(error));
            });
        },
        login(email: string, password: string, turnstileToken: string) {
            return new Promise((resolve, reject) => {
                axiosClient
                    .post('/auth/login', {
                        email,
                        password,
                        'cf-turnstile-response': turnstileToken,
                    })
                    .then((res) => {
                        this.resetStores();
                        this.token = res.data.token;
                        localStorage.setItem('token', res.data.token);
                        axiosClient.defaults.headers.common['Authorization'] = 'Bearer ' + this.token;
                        resolve(res);
                    })
                    .catch((error) => reject(error));
            });
        },
        register(
            firstName: string,
            lastName: string,
            email: string,
            phoneNumber: string,
            bsn: string,
            password: string,
            passwordConfirmation: string,
            turnstileToken: string,
        ) {
            return new Promise((resolve, reject) => {
                axiosClient
                    .post('/auth/register', {
                        firstName,
                        lastName,
                        email,
                        phoneNumber,
                        bsn,
                        password,
                        passwordConfirmation,
                        'cf-turnstile-response': turnstileToken,
                    })
                    .then((res) => {
                        this.resetStores();
                        this.token = res.data.token;
                        localStorage.setItem('token', res.data.token);
                        axiosClient.defaults.headers.common['Authorization'] = 'Bearer ' + this.token;
                        resolve(res);
                    })
                    .catch((error) => reject(error));
            });
        },
        autoLogin() {
            if (!this.token) {
                return;
            }

            axiosClient.defaults.headers.common['Authorization'] = 'Bearer ' + this.token;

            this.me()
                .then((res) => {
                    this.resetStores();
                    this.setUserResponse(res);
                })
                .catch((error) => {
                    if (error.response.status === 401) {
                        this.logout();
                    }
                });
        },
        setUserResponse(res: AxiosResponse) {
            this.id = res.data.id;
            this.firstName = res.data.firstName;
            this.lastName = res.data.lastName;
            this.email = res.data.email;
            this.phoneNumber = res.data.phoneNumber;
            this.bsn = res.data.bsn;
            this.roles = res.data.roles;
            this.dailyLimit = res.data.dailyLimit;
            this.transferLimit = res.data.transferLimit;
            this.createdAt = res.data.createdAt;
            this.status = res.data.status;
        },
        logout() {
            localStorage.removeItem('token');
            axiosClient.defaults.headers.common['Authorization'] = '';
            this.resetStores();
        },
        resetStores() {
            this.$reset();
        },
    },
});
