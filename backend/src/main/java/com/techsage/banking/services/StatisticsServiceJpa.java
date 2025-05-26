package com.techsage.banking.services;

import com.techsage.banking.models.dto.*;
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

    private ChartJsDataDto getTransactionTypeChart() {
        ChartJsDataDto chartData = new ChartJsDataDto();

        ChartDatasetDto dataset = new ChartDatasetDto();
        dataset.setLabel("Transactions");

        for (TransactionType transactionType : TransactionType.values()) {
            chartData.addLabel(transactionType.name());
            dataset.addData(transactionRepository.sumByTransactionType(transactionType));
        }

        chartData.addDataset(dataset);

        return chartData;
    }

    private ChartJsDataDto getTransactionTodayChart() {
        ChartJsDataDto chartData = new ChartJsDataDto();
        chartData.setLabels(this.generateHourlyLabels());

        chartData.addDataset(this.getTransactionsTodayValueChart());
        chartData.addDataset(this.getTransactionsTodayCountChart());

        return chartData;
    }

    private ChartDatasetDto getTransactionsTodayValueChart() {
        ChartDatasetDto dataset = new ChartDatasetDto();
        dataset.setLabel("Transaction (value)");

        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfHour = now.withHour(hour).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfHour = now.withHour(hour).withMinute(59).withSecond(59).withNano(999999999);
            BigDecimal sum = transactionRepository.sumByTransactionTime(startOfHour, endOfHour);
            dataset.addData(sum == null ? BigDecimal.ZERO : sum);
        }
        dataset.setYAxisID("y");

        return dataset;
    }

    private ChartDatasetDto getTransactionsTodayCountChart() {
        ChartDatasetDto dataset = new ChartDatasetDto();
        dataset.setLabel("Transaction (count)");

        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfHour = now.withHour(hour).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfHour = now.withHour(hour).withMinute(59).withSecond(59).withNano(999999999);
            BigDecimal sum = transactionRepository.countByTransactionTime(startOfHour, endOfHour);
            dataset.addData(sum == null ? BigDecimal.ZERO : sum);
        }
        dataset.setYAxisID("y1");

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
