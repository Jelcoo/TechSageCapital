package com.techsage.banking.services;

import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.repositories.*;
import com.techsage.banking.services.interfaces.*;
import org.springframework.stereotype.*;

import java.math.*;
import java.util.*;

@Service
public class StatisticsServiceJpa implements StatisticsService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;

    public StatisticsServiceJpa(UserRepository userRepository, TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    public StatisticsResponseDto stats() {
        StatisticsResponseDto statisticsResponseDto = new StatisticsResponseDto();

        statisticsResponseDto.setCustomerCount(userRepository.countByRoles(UserRole.ROLE_CUSTOMER));
        statisticsResponseDto.setTransactionCount(transactionRepository.count());
        statisticsResponseDto.setTotalBankCapital(bankAccountRepository.getSumOfBalance());
        statisticsResponseDto.setTransactionTypeChart(this.getTransactionTypeChart());

        return statisticsResponseDto;
    }

    private Map<String, Object> getTransactionTypeChart() {
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", TransactionType.values());

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "Transactions");

        List<BigDecimal> data = new ArrayList<>();
        for (TransactionType transactionType : TransactionType.values()) {
            data.add(transactionRepository.sumByTransactionType(transactionType));
        }
        dataset.put("data", data);

        chartData.put("datasets", List.of(dataset));

        return chartData;
    }
}
