package com.techsage.banking.repositories;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;

import java.math.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long>, ListPagingAndSortingRepository<Transaction, Long> {
    @Query("SELECT coalesce(SUM(t.amount), 0) FROM Transaction t WHERE t.fromAccount = ?1 AND t.createdAt <= ?2 AND (t.type = 'WITHDRAWAL' OR t.type='ATM_WITHDRAWAL')")
    BigDecimal findSumOfTransactionsByFromAccount(BankAccount bankAccount, LocalDateTime date);

    @Query("SELECT t FROM Transaction t WHERE ((t.type = 'WITHDRAWAL' OR t.type = 'ATM_WITHDRAWAL') AND t.fromAccount = ?1) OR ((t.type = 'DEPOSIT' OR t.type = 'ATM_DEPOSIT') AND t.toAccount = ?1) ORDER BY t.createdAt DESC")
    Page<Transaction> findAllByBankAccount(BankAccount bankAccount, Pageable pageable);

    Page<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
