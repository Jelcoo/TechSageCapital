package com.techsage.banking.models.dto.requests;

import com.techsage.banking.models.dto.*;
import lombok.*;

@Data
public class RegisterRequestDto extends BaseDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String bsn;
    private String password;
}
