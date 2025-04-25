package com.techsage.banking.services.interfaces;

import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();
    User getById(long id);
    User create(User user);
    User update(User user);
    void delete(long id);
}
