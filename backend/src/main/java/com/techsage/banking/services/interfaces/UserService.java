package com.techsage.banking.services.interfaces;

import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.dto.requests.UpdateUserRequestDto;
import com.techsage.banking.models.enums.*;
import org.springframework.data.domain.*;

import javax.naming.*;

public interface UserService {
    UserDto getById(long id);
    User create(User user);
    UserDto update(long id, UpdateUserRequestDto user);
    UserDto updateSelf(String currentEmail, UpdateSelfRequestDto user);
    UserDto softDelete(long id);
    UserDto reinstateUser(long id);
    AuthResponseDto login(LoginRequestDto loginRequestDto) throws AuthenticationException;
    AuthResponseDto refreshToken(RefreshRequestDto refreshToken) throws AuthenticationException;
    AuthResponseDto register(RegisterRequestDto registerRequestDto) throws AuthenticationException;
    UserDto getByEmail(String email);
    User getByEmailRaw(String email);
    Page<UserDto> findByStatus(UserStatus status, Pageable pageable);
    UserDto approveUser(long id, ApprovalRequestDto approvalRequestDto) throws IllegalArgumentException;
    UserDto updateLimits(long id, UserLimitsRequestDto userLimitsRequestDto) throws IllegalArgumentException;
}
