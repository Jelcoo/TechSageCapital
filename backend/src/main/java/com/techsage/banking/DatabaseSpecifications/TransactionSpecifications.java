package com.techsage.banking.DatabaseSpecifications;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.dto.requests.TransactionFilterRequestDto;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecifications {

    public static Specification<Transaction> filterByBankAccountAndCriteria(BankAccount bankAccount, TransactionFilterRequestDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add account-specific filter (withdrawals & deposits)
            predicates.add(buildAccountPredicate(root, cb, bankAccount));

            // Add other filters
            addDateFilters(filter, root, cb, predicates);
            addIbanFilter(filter, root, cb, predicates);
            addAmountFilter(filter, root, cb, predicates);

            // Order by createdAt descending
            if (query != null) {
                query.orderBy(cb.desc(root.get("createdAt")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate buildAccountPredicate(Root<Transaction> root, CriteriaBuilder cb, BankAccount bankAccount) {
        Predicate withdrawPredicate = cb.and(
                cb.or(
                        cb.equal(root.get("type"), "WITHDRAWAL"),
                        cb.equal(root.get("type"), "ATM_WITHDRAWAL")
                ),
                cb.equal(root.get("fromAccount"), bankAccount)
        );

        Predicate depositPredicate = cb.and(
                cb.or(
                        cb.equal(root.get("type"), "DEPOSIT"),
                        cb.equal(root.get("type"), "ATM_DEPOSIT")
                ),
                cb.equal(root.get("toAccount"), bankAccount)
        );

        return cb.or(withdrawPredicate, depositPredicate);
    }

    private static void addDateFilters(TransactionFilterRequestDto filter, Root<Transaction> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (filter.getStartDate() != null && !filter.getStartDate().isEmpty()) {
            LocalDateTime startDate = LocalDate.parse(filter.getStartDate()).atStartOfDay();
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
        }

        if (filter.getEndDate() != null && !filter.getEndDate().isEmpty()) {
            LocalDateTime endDate = LocalDate.parse(filter.getEndDate()).atTime(23, 59, 59);
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
        }
    }

    private static void addIbanFilter(TransactionFilterRequestDto filter, Root<Transaction> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (filter.getIbanFilter() != null && !filter.getIbanFilter().isEmpty()) {
            predicates.add(cb.or(
                    cb.equal(root.get("fromAccount").get("iban"), filter.getIbanFilter()),
                    cb.equal(root.get("toAccount").get("iban"), filter.getIbanFilter())
            ));
        }
    }

    private static void addAmountFilter(TransactionFilterRequestDto filter, Root<Transaction> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (filter.getAmountFilterType() != null && filter.getAmountFilterValue() != null) {
            BigDecimal amount = new BigDecimal(filter.getAmountFilterValue());

            switch (filter.getAmountFilterType()) {
                case EQUALS -> predicates.add(cb.equal(root.get("amount"), amount));
                case GREATER_THAN -> predicates.add(cb.greaterThan(root.get("amount"), amount));
                case LESS_THAN -> predicates.add(cb.lessThan(root.get("amount"), amount));
                default -> throw new IllegalArgumentException(
                        "Unsupported amount filter type: " + filter.getAmountFilterType());
            }
        }
    }
}
