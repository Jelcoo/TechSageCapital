package com.techsage.banking.models.info;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.enums.BankAccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iban4j.Iban;

@Data
@NoArgsConstructor
public class BankAccountInfoWithoutBalance {
    private Long id;
    private String iban;
    private String firstName;
    private String lastName;
    private BankAccountType type;
}
