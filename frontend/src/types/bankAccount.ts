export interface BankAccount {
    id: number;
    owner_id: number; // Foreign key to User table
    iban: string;
    balance: number;
    absoluteMinimum: number; // Minimum balance required in the account
    type: BankAccountType; // 'savings' | 'checking' | optional ATM
}

export enum BankAccountType {
    SAVINGS = 'SAVINGS',
    CHECKING = 'CHECKING',
    ATM = 'ATM',
}