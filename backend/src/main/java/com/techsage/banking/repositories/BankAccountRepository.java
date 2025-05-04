package com.techsage.banking.repositories;

import com.techsage.banking.models.BankAccount;
import org.iban4j.Iban;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {
    BankAccount findByIban(Iban iban);
}
