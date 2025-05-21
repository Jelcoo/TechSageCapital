package com.techsage.banking.models.dto.responses;

import com.techsage.banking.models.dto.*;
import org.springframework.data.domain.*;

public class BankAccountPagedDto extends PageResponseDto<BankAccountDto> {
    public BankAccountPagedDto(Page<BankAccountDto> page) {
        super(page);
    }
}
