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
    status: Status;
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
