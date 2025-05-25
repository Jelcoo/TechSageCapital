package com.techsage.banking.models.dto.responses;

import com.techsage.banking.models.dto.*;
import org.springframework.data.domain.*;

public class UserPagedDto extends PageResponseDto<UserDto> {
    public UserPagedDto(Page<UserDto> page) {
        super(page);
    }
}
