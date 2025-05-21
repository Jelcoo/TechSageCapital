package com.techsage.banking.services.interfaces;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.BankAccountDto;
import com.techsage.banking.models.enums.BankAccountType;
import com.techsage.banking.models.info.BankAccountInfoWithoutBalance;
import org.iban4j.Iban;
import org.springframework.data.domain.*;

import java.math.*;
import java.util.List;

public interface BankAccountService {
    Page<BankAccountDto> findByUserAndType(User user, BankAccountType type, Pageable pageable);
    BankAccount getByIban(Iban iban);
    Page<BankAccountInfoWithoutBalance> findByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);
    BankAccount create(User user, BankAccountType bankAccountType, BigDecimal absoluteMinimumBalance, BigDecimal balance);
    BankAccount update(BankAccount bankAccount);
    BankAccount getById(long id);
}
