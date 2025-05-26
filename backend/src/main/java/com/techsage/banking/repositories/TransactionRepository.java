package com.techsage.banking.repositories;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.enums.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;

import java.math.*;
import java.time.*;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long>, ListPagingAndSortingRepository<Transaction, Long> {
    @Query("SELECT coalesce(SUM(t.amount), 0) FROM Transaction t WHERE t.fromAccount = ?1 AND t.createdAt <= ?2 AND (t.type = 'WITHDRAWAL' OR t.type='ATM_WITHDRAWAL')")
    BigDecimal findSumOfTransactionsByFromAccount(BankAccount bankAccount, LocalDateTime date);

    @Query("SELECT t FROM Transaction t WHERE ((t.type = 'WITHDRAWAL' OR t.type = 'ATM_WITHDRAWAL') AND t.fromAccount = ?1) OR ((t.type = 'DEPOSIT' OR t.type = 'ATM_DEPOSIT') AND t.toAccount = ?1) ORDER BY t.createdAt DESC")
    Page<Transaction> findAllByBankAccount(BankAccount bankAccount, Pageable pageable);

    Page<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = ?1")
    BigDecimal sumByTransactionType(TransactionType transactionType);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.createdAt >= ?1 AND t.createdAt <= ?2")
    BigDecimal sumByTransactionTime(LocalDateTime startOfHour, LocalDateTime endOfHour);

    @Query("SELECT COUNT(t.id) FROM Transaction t WHERE t.createdAt >= ?1 AND t.createdAt <= ?2")
    BigDecimal countByTransactionTime(LocalDateTime startOfHour, LocalDateTime endOfHour);
}
