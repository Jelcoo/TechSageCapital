package com.techsage.banking.repositories;

import com.techsage.banking.models.User;
import com.techsage.banking.models.enums.*;
import org.springframework.data.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> getByEmail(String email);
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    long countByRoles(UserRole role);
}
