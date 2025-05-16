package com.techsage.banking.deserializers;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.iban4j.Iban;

import java.io.IOException;

public class IbanDeserializer extends JsonDeserializer<Iban> {

    @Override
    public Iban deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String ibanString = p.getText().replaceAll("\\s+", "");
        try {
            return Iban.valueOf(ibanString);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid IBAN format: " + ibanString, e);
        }
    }
}
