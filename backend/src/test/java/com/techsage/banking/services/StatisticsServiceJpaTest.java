package com.techsage.banking.services;

import com.techsage.banking.models.dto.ChartDatasetDto;
import com.techsage.banking.models.dto.ChartJsDataDto;
import com.techsage.banking.models.dto.responses.StatisticsResponseDto;
import com.techsage.banking.models.enums.TransactionType;
import com.techsage.banking.models.enums.UserRole;
import com.techsage.banking.repositories.BankAccountRepository;
import com.techsage.banking.repositories.TransactionRepository;
import com.techsage.banking.repositories.UserRepository;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticsServiceJpaTest extends ServiceTestBase {

    private UserRepository userRepository;
    private TransactionRepository transactionRepository;
    private BankAccountRepository bankAccountRepository;
    private StatisticsServiceJpa statisticsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        bankAccountRepository = mock(BankAccountRepository.class);
        statisticsService = new StatisticsServiceJpa(userRepository, transactionRepository, bankAccountRepository);
    }

    @Test
    void testStats() {
        // Arrange
        when(userRepository.countByRoles(UserRole.ROLE_CUSTOMER)).thenReturn(5L);
        when(transactionRepository.count()).thenReturn(100L);
        when(bankAccountRepository.getSumOfBalance()).thenReturn(new BigDecimal("100000.00"));

        for (TransactionType type : TransactionType.values()) {
            when(transactionRepository.sumByTransactionType(type)).thenReturn(BigDecimal.TEN);
        }

        LocalDateTime now = LocalDateTime.now();
        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime startOfHour = now.withHour(hour).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfHour = now.withHour(hour).withMinute(59).withSecond(59).withNano(999_999_999);

            when(transactionRepository.sumByTransactionTime(startOfHour, endOfHour)).thenReturn(BigDecimal.valueOf(hour));
            when(transactionRepository.countByTransactionTime(startOfHour, endOfHour)).thenReturn(BigDecimal.valueOf(hour * 2));
        }

        // Act
        StatisticsResponseDto response = statisticsService.stats();

        // Assert
        assertNotNull(response);
        assertEquals(5L, response.getCustomerCount());
        assertEquals(100L, response.getTransactionCount());
        assertEquals(new BigDecimal("100000.00"), response.getTotalBankCapital());

        // Chart data checks
        ChartJsDataDto typeChart = response.getTransactionTypeChart();
        assertEquals(TransactionType.values().length, typeChart.getLabels().size());

        ChartJsDataDto todayChart = response.getTransactionTodayChart();
        assertEquals(24, todayChart.getLabels().size());
        assertEquals(2, todayChart.getDatasets().size());

        ChartDatasetDto valueDataset = todayChart.getDatasets().get(0);
        ChartDatasetDto countDataset = todayChart.getDatasets().get(1);
        assertEquals("Transaction (value)", valueDataset.getLabel());
        assertEquals("Transaction (count)", countDataset.getLabel());
        assertEquals(24, valueDataset.getData().size());
        assertEquals(24, countDataset.getData().size());
    }
}
