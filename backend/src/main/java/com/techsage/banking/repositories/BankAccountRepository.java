package com.techsage.banking.repositories;

import com.techsage.banking.models.*;
import com.techsage.banking.models.enums.*;
import org.iban4j.Iban;
import org.springframework.data.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {
    BankAccount findByIban(Iban iban);
    Page<BankAccount> findByUserFirstNameStartingWithIgnoreCaseAndUserLastNameStartingWithIgnoreCaseAndType(String firstName, String lastName, BankAccountType bankAccountType, Pageable pageable);
    Page<BankAccount> findByUser(User user, Pageable pageable);
    Page<BankAccount> findByUserAndType(User user, BankAccountType type, Pageable pageable);
}
