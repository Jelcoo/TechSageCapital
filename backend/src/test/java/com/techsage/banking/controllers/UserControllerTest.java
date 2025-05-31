package com.techsage.banking.controllers;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(2)
class UserControllerTest extends ControllerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAll() throws Exception {
        this.mockMvc.perform(get("/users").with(authorized()))
                .andExpect(status().isOk());
    }
}
