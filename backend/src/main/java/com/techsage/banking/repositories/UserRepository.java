package com.techsage.banking.repositories;

import com.techsage.banking.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);
}
