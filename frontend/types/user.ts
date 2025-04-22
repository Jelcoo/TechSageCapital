export interface User {
    id: number;
    FirstName: string;
    LastName: string;
    email: string;
    PhoneNumber: string;
    BSN: string;
    Role: Role;
    DailyLimit: number;
    TransferLimit: number;
    createdAt: Date;
    Status: Status;
}

export interface Login {
    email: string;
    password: string;
}

export interface Register extends Login {
    FirstName: string;
    LastName: string;
    email: string;
    PhoneNumber: string;
    BSN: string;
    password: string;
    Role: Role;
}

export interface UpdateUser {
    id: number;
    FirstName?: string;
    LastName?: string;
    email?: string;
    PhoneNumber?: string;
    BSN?: string;
    Role?: Role;
    DailyLimit?: number;
    TransferLimit?: number;
    Status?: Status; 
}

export enum Role {
    ADMIN = 'admin',
    EMPLOYEE = 'employee',
    CUSTOMER = 'customer',
}

export enum Status {
    ACTIVE = 'active',
    INACTIVE = 'inactive',
    SUSPENDED = 'suspended',
}