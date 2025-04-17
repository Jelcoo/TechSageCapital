package com.techsage.banking.services.interfaces;

import com.techsage.banking.models.BankAccount;

import java.util.List;

public interface BankAccountService {
    List<BankAccount> getAll();
    BankAccount getById(int id);
    BankAccount create(BankAccount bankAccount);
    BankAccount update(BankAccount bankAccount);
    void delete(int id);
}
