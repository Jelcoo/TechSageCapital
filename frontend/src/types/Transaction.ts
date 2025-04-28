export interface Transaction {
    id: number;
    from?: number; // Foreign key to BankAccount table (sender's account ID)    can be null for ATM deposit
    to?: number; // Foreign key to BankAccount table (receiver's account ID)    can be null for ATM withdrawal
    initiator: number; // Foreign key to User table (who initiated the transaction)
    amount: number;
    timeStamp: Date;
    type: Type;
    description: string;
    status: TransactionStatus;
}

export enum Type {
    DEPOSIT = 'Deposit',
    WITHDRAWAL = 'Withdrawal',
    ATM_WITHDRAWAL = 'ATM Withdrawal',
    ATM_DEPOSIT = 'ATM Deposit',
}

export enum TransactionStatus {
    PENDING = 'Pending',
    COMPLETED = 'Completed',
    FAILED = 'Failed',
}

export interface CreateTransaction {
    from?: number; // Foreign key to BankAccount table (sender's account ID)    can be null for ATM deposit
    to?: number; // Foreign key to BankAccount table (receiver's account ID)    can be null for ATM withdrawal
    initiator: number; // Foreign key to User table (who initiated the transaction)
    amount: number;
    type: Type;
    description: string;
}