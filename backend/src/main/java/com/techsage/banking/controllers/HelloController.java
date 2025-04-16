package com.techsage.banking.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/yello")
    public String hello() {
        return "Bye world";
    }
}
