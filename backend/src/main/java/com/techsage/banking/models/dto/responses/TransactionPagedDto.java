package com.techsage.banking.models.dto.responses;

import com.techsage.banking.models.dto.*;
import org.springframework.data.domain.*;

public class TransactionPagedDto extends PageResponseDto<TransactionDto> {
    public TransactionPagedDto(Page<TransactionDto> page) {
        super(page);
    }
}
