export interface Transaction {
    id: number;
    fromAccount?: TransactionAccount; // Foreign key to BankAccount table (sender's account ID)    can be null for ATM deposit
    toAccount?: TransactionAccount; // Foreign key to BankAccount table (receiver's account ID)    can be null for ATM withdrawal
    initiator: TransactionInitiator; // Foreign key to User table (who initiated the transaction)
    amount: number;
    createdAt: Date;
    type: Type;
    description: string;
    status: TransactionStatus;
}

export enum Type {
    DEPOSIT = 'DEPOSIT',
    WITHDRAWAL = 'WITHDRAWAL',
    ATM_WITHDRAWAL = 'ATM_WITHDRAWAL',
    ATM_DEPOSIT = 'ATM_DEPOSIT',
}

export enum TransactionStatus {
    PENDING = 'PENDING',
    COMPLETED = 'COMPLETED',
    FAILED = 'FAILED',
}

export interface TransactionAccount {
    firstName: string;
    iban: string;
    id: number;
    lastName: string;
}

export interface TransactionInitiator {
    id: number;
    firstName: string;
    lastName: string;
    dailyLimit: number;
    transferLimit: number;
}


export interface CreateTransaction {
    from?: number; // Foreign key to BankAccount table (sender's account ID)    can be null for ATM deposit
    to?: number; // Foreign key to BankAccount table (receiver's account ID)    can be null for ATM withdrawal
    initiator: number; // Foreign key to User table (who initiated the transaction)
    amount: number;
    type: Type;
    description: string;
}
