package com.techsage.banking.helpers;

import org.iban4j.*;

public class IbanHelper {
    public static Iban generateIban() {
        return new Iban.Builder().countryCode(CountryCode.NL).bankCode("INHO").buildRandom();
    }
}
