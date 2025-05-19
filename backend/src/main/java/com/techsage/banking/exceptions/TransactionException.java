package com.techsage.banking.exceptions;

public class TransactionException extends RuntimeException {

    public enum Reason {
        CHECK_DAILY_LIMIT("Exceeded daily transaction limit"),
        CHECK_TRANSFER_LIMIT("Exceeded transfer limit"),
        CHECK_WITHDRAWAL_LIMIT("Exceeded withdrawal limit or insufficient balance"),
        CHECK_OWN_SAVINGS_ACCOUNT("Can only transfer to own checking/savings account"),
        CHECK_OWNERSHIP("Initiator is not the owner of the bank account"),
        CHECK_SAME_ACCOUNT("Cannot transfer to the same account"),
        BANK_ACCOUNT_NOT_FOUND("Bank account not found"),
        TRANSACTION_FAILED("Transaction failed"),
        TRANSACTIONS_NOT_FOUND("Transactions not found");

        private final String message;

        Reason(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private final Reason reason;

    public TransactionException(Reason reason) {
        super(reason.getMessage());
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "TransactionException: " + reason.getMessage();
    }
}

