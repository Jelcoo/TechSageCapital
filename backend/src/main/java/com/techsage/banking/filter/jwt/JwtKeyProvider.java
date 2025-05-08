package com.techsage.banking.filter.jwt;

import jakarta.annotation.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;

import java.security.*;
import java.security.cert.Certificate;

@Component
public class JwtKeyProvider {
    @Value("${jwt.key-store}")
    private String keystore;

    @Value("${jwt.key-store-password}")
    private String password;

    @Value("${jwt.key-alias}")
    private String alias;

    @Getter
    private Key privateKey;

    @Getter
    private PublicKey publicKey;

    @PostConstruct
    protected void init() {
        try {
            ClassPathResource resource = new ClassPathResource(keystore);
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(resource.getInputStream(), password.toCharArray());
            privateKey = keyStore.getKey(alias, password.toCharArray());

            Certificate cert = keyStore.getCertificate(alias);
            publicKey = cert.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
