package com.techsage.banking.models.info;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.techsage.banking.deserializers.IbanDeserializer;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.enums.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iban4j.Iban;

import java.util.List;

@Data
@NoArgsConstructor
public class BankAccountInfo {
    private Long id;

    @JsonDeserialize(using = IbanDeserializer.class)
    private Iban iban;

    private Double balance;
    private int absoluteMinimumBalance;
    private BankAccountType type;
    public String getIban() {
        return iban != null ? iban.toFormattedString() : null;
    }
}
