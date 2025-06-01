package com.techsage.banking.DatabaseSpecifications;

import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.dto.requests.AllTransactionFilterRequestDto;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AllTransactionSpecification {

    public static Specification<Transaction> filterByCriteria(AllTransactionFilterRequestDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addDateFilters(filter, root, cb, predicates);
            addIbanFilters(filter, root, cb, predicates);
            addAmountFilter(filter, root, cb, predicates);

            // Order by createdAt descending
            if (query != null) {
                query.orderBy(cb.desc(root.get("createdAt")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addDateFilters(AllTransactionFilterRequestDto filter, Root<Transaction> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (filter.getStartDate() != null && !filter.getStartDate().isEmpty()) {
            LocalDateTime startDate = LocalDate.parse(filter.getStartDate()).atStartOfDay();
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
        }

        if (filter.getEndDate() != null && !filter.getEndDate().isEmpty()) {
            LocalDateTime endDate = LocalDate.parse(filter.getEndDate()).atTime(23, 59, 59);
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
        }
    }

    private static void addIbanFilters(AllTransactionFilterRequestDto filter, Root<Transaction> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (filter.getFromIbanFilter() != null && !filter.getFromIbanFilter().isEmpty()) {
            predicates.add(cb.equal(root.get("fromAccount").get("iban"), filter.getFromIbanFilter()));
        }

        if (filter.getToIbanFilter() != null && !filter.getToIbanFilter().isEmpty()) {
            predicates.add(cb.equal(root.get("toAccount").get("iban"), filter.getToIbanFilter()));
        }
    }

    private static void addAmountFilter(AllTransactionFilterRequestDto filter, Root<Transaction> root, CriteriaBuilder cb, List<Predicate> predicates) {
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
