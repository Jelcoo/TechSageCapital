package com.techsage.banking.repositories;

import com.techsage.banking.models.*;
import com.techsage.banking.models.enums.*;
import org.iban4j.Iban;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.*;
import java.util.*;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {
    BankAccount findByIban(Iban iban);
    Page<BankAccount> findByUserFirstNameStartingWithIgnoreCaseAndUserLastNameStartingWithIgnoreCaseAndType(String firstName, String lastName, BankAccountType bankAccountType, Pageable pageable);
    List<BankAccount> findByUser(User user);
    Page<BankAccount> findByUser(User user, Pageable pageable);
    List<BankAccount> findByUserAndType(User user, BankAccountType type);
    Page<BankAccount> findByUserAndType(User user, BankAccountType type, Pageable pageable);

    @Query("SELECT SUM(ba.balance) FROM BankAccount ba")
    BigDecimal getSumOfBalance();
}
