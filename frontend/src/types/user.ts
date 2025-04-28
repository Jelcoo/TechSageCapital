export interface User {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
    bsn: string;
    role: Role;
    dailyLimit: number;
    transferLimit: number;
    createdAt: Date;
    status: AccountStatus;
}

export interface Login {
    email: string;
    password: string;
}

export interface Register extends Login {
    firstName: string;
    lastName: string;
    email: string;
    PhoneNumber: string;
    bsn: string;
    password: string;
    role: Role;
}

export interface UpdateUser {
    id: number;
    firstName?: string;
    lastName?: string;
    email?: string;
    phoneNumber?: string;
    bsn?: string;
    role?: Role;
    dailyLimit?: number;
    transferLimit?: number;
    status?: AccountStatus;
}

export enum Role {
    ADMIN = 'ROLE_ADMIN',
    EMPLOYEE = 'ROLE_EMPLOYEE',
    CUSTOMER = 'ROLE_CUSTOMER',
}

export enum AccountStatus {
    ACTIVE = 'ACTIVE',
    PENDING = 'PENDING',
    DELETED = 'DELETED',
}
