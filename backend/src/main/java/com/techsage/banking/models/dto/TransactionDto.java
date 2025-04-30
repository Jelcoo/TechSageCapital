package com.techsage.banking.models.dto;

import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.models.info.BankAccountInfoWithoutBalance;
import com.techsage.banking.models.info.UserInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionDto extends BaseDto {
    private Long id;
    private BankAccountInfoWithoutBalance fromAccount;
    private BankAccountInfoWithoutBalance toAccount;
    private UserInfo initiator;
    private Double amount;
    private LocalDateTime createdAt;
    private TransactionType type;
    private String description;
}
