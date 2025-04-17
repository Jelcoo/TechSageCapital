package com.techsage.banking.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.iban4j.Iban;

@Converter(autoApply = false)
public class IbanConverter implements AttributeConverter<Iban, String> {

    @Override
    public String convertToDatabaseColumn(Iban iban) {
        return iban != null ? iban.toString() : null;
    }

    @Override
    public Iban convertToEntityAttribute(String dbData) {
        return dbData != null ? Iban.valueOf(dbData) : null;
    }
}
