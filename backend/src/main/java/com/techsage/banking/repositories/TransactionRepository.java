package com.techsage.banking.repositories;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    @Query("SELECT coalesce(SUM(t.amount), 0) FROM Transaction t WHERE t.fromAccount = ?1 AND t.createdAt <= ?2 AND (t.type = 'WITHDRAWAL' OR t.type='ATM_WITHDRAWAL')")
    BigDecimal findSumOfTransactionsByFromAccount(BankAccount bankAccount, LocalDateTime date);

    @Query("SELECT t FROM Transaction t WHERE t.fromAccount = ?1 OR t.toAccount = ?1")
    List<Transaction> findAllByBankAccount(long id);
}
