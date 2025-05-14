package com.techsage.banking.jwt;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

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
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new ClassPathResource(keystore).getInputStream(), password.toCharArray());

            privateKey = keyStore.getKey(alias, password.toCharArray());

            Certificate cert = keyStore.getCertificate(alias);
            publicKey = cert.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load keys from keystore", e);
        }
    }
}
