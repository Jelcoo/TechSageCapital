package com.techsage.banking.repositories;

import com.techsage.banking.models.*;
import com.techsage.banking.models.enums.*;
import org.iban4j.Iban;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {
    BankAccount findByIban(Iban iban);
    List<BankAccount> findByUserFirstNameStartingWithIgnoreCaseAndUserLastNameStartingWithIgnoreCaseAndType(String firstName, String lastName, BankAccountType bankAccountType);
    List<BankAccount> findByUser(User user);
    List<BankAccount> findByUserAndType(User user, BankAccountType type);
    List<BankAccount> findByType(BankAccountType type);
}
