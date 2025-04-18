package com.techsage.banking.services.interfaces;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.dto.BankAccountDto;

import java.util.List;

public interface BankAccountService {
    List<BankAccountDto> getAll();
    BankAccount getById(int id);
    BankAccount create(BankAccount bankAccount);
    BankAccount update(BankAccount bankAccount);
    void delete(int id);
}
