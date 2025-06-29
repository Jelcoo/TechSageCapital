package com.techsage.banking.models.dto.responses;

import com.techsage.banking.models.dto.*;
import lombok.*;

import java.math.*;

@Data
public class StatisticsResponseDto extends BaseDto {
    private long customerCount;
    private long transactionCount;
    private BigDecimal totalBankCapital;
    private ChartJsDataDto transactionTypeChart;
    private ChartJsDataDto transactionTodayChart;
}
