package com.techsage.banking.controllers;

import org.springframework.web.bind.annotation.*;
import com.techsage.banking.models.User;

//basic template for user controller
@RestController
public class UserController {
    @GetMapping("/user/{id}")
    @ResponseBody
    public User getUser(@PathVariable int id) {
        User user = new User();

        user.setId(id); //remove this later. now it doesn't give ane error

        return user;
    }

    @GetMapping("/users")
    @ResponseBody
    public User[] getUsers() {
        User[] users = new User[2];
        users[0] = new User();
        users[0].setId(1);
        users[1] = new User();
        users[1].setId(2);
        return users;
    }


    @GetMapping("/updateUser")
    @ResponseStatus
    public void updateUser (User user) {
        //return a 200 or 404 or other code based on if query succeeded
    }

    @PostMapping("/createUser")
    @ResponseStatus
    public void createUser (User user) {
        //return a 200 or 404 or other code based on if query succeeded
    }
}
