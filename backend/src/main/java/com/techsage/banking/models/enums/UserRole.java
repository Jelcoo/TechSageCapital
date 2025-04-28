package com.techsage.banking.models.enums;

import org.springframework.security.core.*;

public enum UserRole implements GrantedAuthority {
    ROLE_CUSTOMER,
    ROLE_EMPLOYEE,
    ROLE_ADMIN;

    public String getAuthority() {
        return name();
    }
}
