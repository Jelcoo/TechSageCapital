export interface BankAccount {
    id: number;
    Owner_ID: number; // Foreign key to User table
    IBAN: string; 
    Balance: number; 
    AbsoluteMinimum: number; // Minimum balance required in the account
    Type: string; // 'savings' | 'checking' | optional ATM
}