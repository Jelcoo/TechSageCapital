package com.techsage.banking.controllers;

import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.techsage.banking.models.User;

import java.util.List;

//basic template for user controller
@RestController
public class UserController {

    @Autowired
    private final UserService userService;


    @GetMapping("/user/{id}")
    public User getUser(@PathVariable long id) {
       return userService.getById(id);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getAll();
    }

    @PatchMapping("/updateUser")
    public void updateUser (User user) {
        userService.update(user);
    }

    @PostMapping("/createUser")
    public void createUser (User user) {
        userService.create(user);
    }

    @DeleteMapping("/deleteUser/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.delete(id);
    }
}
