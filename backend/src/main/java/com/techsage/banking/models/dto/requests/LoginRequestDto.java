package com.techsage.banking.models.dto.requests;

import lombok.*;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
