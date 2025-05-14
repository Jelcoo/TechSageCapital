package com.techsage.banking.exceptions;

public class TransactionException extends RuntimeException {

    public enum Reason {
        CHECK_BALANCE("Insufficient balance"),
        CHECK_DAILY_LIMIT("Exceeded daily transaction limit"),
        CHECK_TRANSFER_LIMIT("Exceeded transfer limit"),
        CHECK_WITHDRAWAL_LIMIT("Exceeded withdrawal limit"),
        BANK_ACCOUNT_NOT_FOUND("Bank account not found"),
        TRANSACTION_FAILED("Transaction failed");

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

