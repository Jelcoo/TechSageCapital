package com.techsage.banking.repositories;

import com.techsage.banking.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    // Find Accounts by Status
    List<User> findAllAccountsByStatus(User.Status status);
}
