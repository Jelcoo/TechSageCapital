export interface BankAccount {
    id: number;
    owner_id: number; // Foreign key to User table
    iban: string;
    balance: number;
    absoluteMinimum: number; // Minimum balance required in the account
    type: BankAccountType; // 'savings' | 'checking' | optional ATM
    bankAccounts: BankAccount[];
}
export interface SearchResponseBankaccount {
    id: number;
    firstName: string;
    lastName: string;
    iban: string;
    type: BankAccountType;
}

export enum BankAccountType {
    SAVINGS = 'SAVINGS',
    CHECKING = 'CHECKING',
    ATM = 'ATM',
}
