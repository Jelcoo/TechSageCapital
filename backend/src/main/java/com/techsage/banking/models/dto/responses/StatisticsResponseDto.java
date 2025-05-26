package com.techsage.banking.models.dto.responses;

import com.techsage.banking.models.dto.*;
import lombok.*;

import java.math.*;
import java.util.*;

@Data
public class StatisticsResponseDto extends BaseDto {
    private long customerCount;
    private long transactionCount;
    private BigDecimal totalBankCapital;
    private Map<String, Object> transactionTypeChart;
    private Map<String, Object> transactionTodayChart;
}
