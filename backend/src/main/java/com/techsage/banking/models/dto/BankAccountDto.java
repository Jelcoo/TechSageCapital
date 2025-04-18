package com.techsage.banking.models.dto;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.info.UserInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iban4j.Iban;

@Data
@NoArgsConstructor
public class BankAccountDto {
    private Integer id;
    private UserInfo user;
    private Iban iban;
    private Double balance;
    private int absoluteMinimumBalance;
    private BankAccount.Type type;
}
