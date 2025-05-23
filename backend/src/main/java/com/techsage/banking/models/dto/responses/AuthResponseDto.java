package com.techsage.banking.models.dto.responses;

import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.enums.*;
import lombok.*;

@Data
public class AuthResponseDto extends BaseDto {
    private String accessToken;
    private String refreshToken;
    private AuthenticationScope scope;
}
