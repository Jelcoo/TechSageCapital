package com.techsage.banking.models.dto.requests;

import com.techsage.banking.models.dto.*;
import lombok.*;

@Data
public class LoginRequestDto extends BaseDto {
    private String email;
    private String password;
}
