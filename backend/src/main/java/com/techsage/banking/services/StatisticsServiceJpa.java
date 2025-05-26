package com.techsage.banking.services;

import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.repositories.*;
import com.techsage.banking.services.interfaces.*;
import org.springframework.stereotype.*;

import java.math.*;
import java.time.*;
import java.time.format.*;
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
        statisticsResponseDto.setTransactionTodayChart(this.getTransactionTodayChart());

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

    private Map<String, Object> getTransactionTodayChart() {
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", this.generateHourlyLabels());

        chartData.put("datasets", List.of(
                this.getTransactionsTodayValueChart(),
                this.getTransactionsTodayCountChart())
        );

        return chartData;
    }

    private Map<String, Object> getTransactionsTodayValueChart() {
        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "Transaction (value)");

        List<BigDecimal> data = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfHour = now.withHour(hour).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfHour = now.withHour(hour).withMinute(59).withSecond(59).withNano(999999999);
            BigDecimal sum = transactionRepository.sumByTransactionTime(startOfHour, endOfHour);
            data.add(sum == null ? BigDecimal.ZERO : sum);
        }
        dataset.put("data", data);
        dataset.put("yAxisID", "y");

        return dataset;
    }

    private Map<String, Object> getTransactionsTodayCountChart() {
        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "Transaction (count)");

        List<BigDecimal> data = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfHour = now.withHour(hour).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfHour = now.withHour(hour).withMinute(59).withSecond(59).withNano(999999999);
            BigDecimal sum = transactionRepository.countByTransactionTime(startOfHour, endOfHour);
            data.add(sum == null ? BigDecimal.ZERO : sum);
        }
        dataset.put("data", data);
        dataset.put("yAxisID", "y1");

        return dataset;
    }

    private List<String> generateHourlyLabels() {
        List<String> labels = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:00");

        for (int hour = 0; hour < 24; hour++) {
            LocalTime time = LocalTime.of(hour, 0);
            labels.add(time.format(formatter));
        }

        return labels;
    }
}
