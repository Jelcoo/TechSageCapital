package com.techsage.banking.models.dto.responses;

import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.info.*;
import org.springframework.data.domain.*;

public class BankAccountWithoutBalancePagedDto extends PageResponseDto<BankAccountInfoWithoutBalance> {
    public BankAccountWithoutBalancePagedDto(Page<BankAccountInfoWithoutBalance> page) {
        super(page);
    }
}
