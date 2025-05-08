import { defineStore } from 'pinia';
import axiosClient from '@/axios';
import type { AxiosResponse } from 'axios';
import { AccountStatus, Role, type User } from '@/types';

interface StoreUser extends User {
    accessToken: string | null;
    refreshToken: string | null;
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
        bankAccounts: [],
        accessToken: localStorage.getItem('accessToken') || null,
        refreshToken: localStorage.getItem('refreshToken') || null,
    }),
    getters: {
        fullName: (state) => `${state.firstName} ${state.lastName}`,
        isAuthenticated: (state) => state.accessToken !== null,
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
                        this.accessToken = res.data.accessToken;
                        this.refreshToken = res.data.refreshToken;
                        localStorage.setItem('accessToken', res.data.accessToken);
                        localStorage.setItem('refreshToken', res.data.refreshToken);
                        axiosClient.defaults.headers.common['Authorization'] = 'Bearer ' + this.accessToken;
                        resolve(res);
                    })
                    .catch((error) => reject(error));
            });
        },
        async refreshTokens() {
            try {
                const response = await axiosClient.post('/auth/refresh', {
                    refreshToken: this.refreshToken,
                });
                this.accessToken = response.data.accessToken;
                this.refreshToken = response.data.refreshToken;
                localStorage.setItem('accessToken', response.data.accessToken);
                localStorage.setItem('refreshToken', response.data.refreshToken);
                axiosClient.defaults.headers.common['Authorization'] = 'Bearer ' + this.accessToken;
                return true;
            } catch (e) {
                console.error('Error refreshing tokens:', e);
                this.logout();
                return false;
            }
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
                        this.accessToken = res.data.accessToken;
                        this.refreshToken = res.data.refreshToken;
                        localStorage.setItem('accessToken', res.data.accessToken);
                        localStorage.setItem('refreshToken', res.data.refreshToken);
                        axiosClient.defaults.headers.common['Authorization'] = 'Bearer ' + this.accessToken;
                        resolve(res);
                    })
                    .catch((error) => reject(error));
            });
        },
        autoLogin() {
            return new Promise((resolve, reject) => {
                if (!this.accessToken) {
                    resolve(null);
                    return;
                }

                axiosClient.defaults.headers.common['Authorization'] = 'Bearer ' + this.accessToken;

                this.me()
                    .then((res) => {
                        this.resetStores();
                        this.setUserResponse(res);
                        resolve(res);
                    })
                    .catch(async (error) => {
                        if (error.response?.status === 401) {
                            const refreshed = await this.refreshTokens();
                            if (refreshed) {
                                this.me()
                                    .then((res) => {
                                        this.setUserResponse(res);
                                        resolve(res);
                                    })
                                    .catch((error) => {
                                        this.logout();
                                        reject(error);
                                    });
                            } else {
                                this.logout();
                                reject(error);
                            }
                        } else {
                            this.logout();
                            reject(error);
                        }
                    });
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
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            axiosClient.defaults.headers.common['Authorization'] = '';
            this.resetStores();
        },
        resetStores() {
            this.$reset();
        },
    },
});
