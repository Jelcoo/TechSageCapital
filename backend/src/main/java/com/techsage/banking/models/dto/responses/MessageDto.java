package com.techsage.banking.models.dto.responses;

import com.techsage.banking.models.dto.*;
import lombok.*;

@Data
@AllArgsConstructor
public class MessageDto extends BaseDto {
    private int status;
    private String message;
}
