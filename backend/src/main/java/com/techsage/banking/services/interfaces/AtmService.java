package com.techsage.banking.services.interfaces;

import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;

import javax.naming.*;

public interface AtmService {
    AtmAuthResponseDto login(LoginRequestDto loginRequestDto) throws AuthenticationException;
}
