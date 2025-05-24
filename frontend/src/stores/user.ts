import { defineStore } from 'pinia';
import axiosClient from '@/axios';
import type { AxiosError, AxiosResponse } from 'axios';
import { AccountStatus, type User } from '@/types';
import router from '@/router';

interface StoreUser extends User {
    accessToken: string | null;
    refreshToken: string | null;
    authenticationScope: AuthenticationScope | null;
}

export enum AuthenticationScope {
    BANK = 'BANK',
    ATM = 'ATM',
}

export const useUserStore = defineStore('user', {
    state: (): StoreUser => ({
        id: 0,
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        bsn: '',
        roles: [],
        dailyLimit: 0,
        transferLimit: 0,
        createdAt: new Date(),
        status: AccountStatus.ACTIVE,
        bankAccounts: [],
        accessToken: localStorage.getItem('accessToken') || null,
        refreshToken: localStorage.getItem('refreshToken') || null,
        authenticationScope: (localStorage.getItem('authenticationScope') as AuthenticationScope) || null,
    }),

    getters: {
        fullName: (state) => `${state.firstName} ${state.lastName}`,
        isAuthenticated: (state) => state.accessToken !== null,
    },

    actions: {
        async me(): Promise<AxiosResponse<User>> {
            return axiosClient.get('/users/me');
        },

        async login(email: string, password: string, turnstileToken: string, scope: AuthenticationScope) {
            try {
                const response = await axiosClient.post('/auth/login', {
                    email,
                    password,
                    'cf-turnstile-response': turnstileToken,
                    scope,
                });

                await this.handleAuthSuccess(response.data);
                await this.autoLogin();
                return response;
            } catch (error) {
                return Promise.reject(error);
            }
        },

        async register(
            firstName: string,
            lastName: string,
            email: string,
            phoneNumber: string,
            bsn: string,
            password: string,
            passwordConfirmation: string,
            turnstileToken: string,
        ) {
            try {
                const response = await axiosClient.post('/auth/register', {
                    firstName,
                    lastName,
                    email,
                    phoneNumber,
                    bsn,
                    password,
                    passwordConfirmation,
                    'cf-turnstile-response': turnstileToken,
                });

                this.handleAuthSuccess(response.data);
                return response;
            } catch (error) {
                return Promise.reject(error);
            }
        },

        async refreshTokens(): Promise<boolean> {
            try {
                const { data } = await axiosClient.post('/auth/refresh', {
                    refreshToken: this.refreshToken,
                });

                this.setTokens(data.accessToken, data.refreshToken, data.claim);
                return true;
            } catch (error) {
                console.error('Error refreshing tokens:', error);
                this.logout();
                return false;
            }
        },

        async autoLogin() {
            if (!this.accessToken) return null;

            axiosClient.defaults.headers.common['Authorization'] = `Bearer ${this.accessToken}`;

            try {
                const res = await this.me();
                await this.resetStores();
                await this.setUserResponse(res.data);
                return res;
            } catch (error: unknown) {
                const err = error as AxiosError;

                if (err.response?.status === 401) {
                    const refreshed = await this.refreshTokens();
                    if (refreshed) {
                        try {
                            const res = await this.me();
                            await this.setUserResponse(res.data);
                            return res;
                        } catch (innerErr) {
                            this.logout();
                            return Promise.reject(innerErr);
                        }
                    }
                }

                this.logout();
                return Promise.reject(err);
            }
        },

        handleAuthSuccess(data: { accessToken: string; refreshToken: string; scope: AuthenticationScope }) {
            this.resetStores();
            this.setTokens(data.accessToken, data.refreshToken, data.scope);
        },

        setTokens(accessToken: string, refreshToken: string, scope: AuthenticationScope) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.authenticationScope = scope;
            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', refreshToken);
            localStorage.setItem('authenticationScope', scope);
            axiosClient.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
        },

        setUserResponse(user: User) {
            Object.assign(this, user);
        },

        logout() {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('authenticationScope');
            delete axiosClient.defaults.headers.common['Authorization'];
            this.resetStores();
            router.push({ name: 'home' });
        },

        resetStores() {
            this.$reset();
        },
    },
});
