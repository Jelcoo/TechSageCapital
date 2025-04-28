package com.techsage.banking.models.enums;

import org.springframework.security.core.*;

public enum UserRole implements GrantedAuthority {
    CUSTOMER,
    EMPLOYEE,
    ADMIN;

    public String getAuthority() {
        return name();
    }
}
