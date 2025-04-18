package com.techsage.banking.models.info;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techsage.banking.models.BankAccount;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iban4j.Iban;

@Data
@NoArgsConstructor
public class BankAccountInfoWithoutBalance {
    private Integer id;
    private Iban iban;

    @JsonIgnore
    private Double balance;

    private int absoluteMinimumBalance;
    private BankAccount.Type type;
}
