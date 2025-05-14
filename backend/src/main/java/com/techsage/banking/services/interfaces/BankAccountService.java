package com.techsage.banking.services.interfaces;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.BankAccountDto;
import com.techsage.banking.models.enums.BankAccountType;
import org.iban4j.Iban;

import java.math.*;
import java.util.List;

public interface BankAccountService {
    List<BankAccountDto> getAll();
    List<BankAccountDto> findByUserAndType(User user, BankAccountType type);
    List<BankAccountDto> findByType(BankAccountType type);
    BankAccount getByIban(Iban iban);
    BankAccount create(User user, BankAccountType bankAccountType, BigDecimal absoluteMinimumBalance, BigDecimal balance);
    BankAccount update(BankAccount bankAccount);
}
