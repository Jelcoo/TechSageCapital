export interface Transaction {
    id: number;
    From?: number; // Foreign key to BankAccount table (sender's account ID)    can be null for ATM deposit
    To?: number; // Foreign key to BankAccount table (receiver's account ID)    can be null for ATM withdrawal
    Initiator: number; // Foreign key to User table (who initiated the transaction)
    Amount: number;
    TimeStamp: Date; 
    Type: Type;
    Description: string;
    Status: Status; 
}

export enum Type{
    DEPOSIT = 'Deposit',
    WITHDRAWAL = 'Withdrawal',
    ATM_WITHDRAWAL = 'ATM Withdrawal',
    ATM_DEPOSIT = 'ATM Deposit',
}

export enum Status{
    PENDING = 'Pending',
    COMPLETED = 'Completed',
    FAILED = 'Failed',
}

export interface CreateTransaction {
    From?: number; // Foreign key to BankAccount table (sender's account ID)    can be null for ATM deposit
    To?: number; // Foreign key to BankAccount table (receiver's account ID)    can be null for ATM withdrawal
    Initiator: number; // Foreign key to User table (who initiated the transaction)
    Amount: number;
    Type: Type;
    Description: string;
}