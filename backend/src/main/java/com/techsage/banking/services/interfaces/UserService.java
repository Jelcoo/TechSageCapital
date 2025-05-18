package com.techsage.banking.services.interfaces;

import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.dto.updateUserDto;
import com.techsage.banking.models.enums.*;

import javax.naming.*;
import java.util.*;

public interface UserService {
    List<UserDto> getAll();
    UserDto getById(long id);
    User create(User user);
    UserDto update(long id, updateUserDto user);
    UserDto getSelf(long id , String email);
    void softDelete(long id);
    UserDto reinstateUser(long id);
    AuthResponseDto login(LoginRequestDto loginRequestDto) throws AuthenticationException;
    AuthResponseDto refreshToken(RefreshRequestDto refreshToken) throws AuthenticationException;
    AuthResponseDto register(RegisterRequestDto registerRequestDto) throws AuthenticationException;
    UserDto getByEmail(String email);
    List<UserDto> findByStatus(UserStatus status);
    UserDto approveUser(long id, ApprovalRequestDto approvalRequestDto) throws IllegalArgumentException;
    UserDto updateLimits(long id, UserLimitsRequestDto userLimitsRequestDto) throws IllegalArgumentException;
}
