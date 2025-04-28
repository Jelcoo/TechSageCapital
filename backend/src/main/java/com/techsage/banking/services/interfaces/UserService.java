package com.techsage.banking.services.interfaces;

import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.enums.*;

import javax.naming.*;
import java.util.List;

public interface UserService {
    List<UserDto> getAll();
    User getById(int id);
    User create(User user);
    User update(User user);
    void delete(int id);
    LoginResponseDto login(LoginRequestDto loginRequestDto) throws AuthenticationException;
    List<UserDto> findAllAccountsByStatus(UserStatus status);
}
