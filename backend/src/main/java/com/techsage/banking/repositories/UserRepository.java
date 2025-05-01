package com.techsage.banking.repositories;

import com.techsage.banking.models.User;
import com.techsage.banking.models.enums.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> getByEmail(String email);
    List<User> findByStatus(UserStatus status);
}
