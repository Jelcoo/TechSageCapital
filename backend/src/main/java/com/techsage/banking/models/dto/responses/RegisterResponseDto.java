package com.techsage.banking.models.dto.responses;

import com.techsage.banking.models.dto.*;
import lombok.*;

@Data
public class RegisterResponseDto extends BaseDto {
    private String token;
}
