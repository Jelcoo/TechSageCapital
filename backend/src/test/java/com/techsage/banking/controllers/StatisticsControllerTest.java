package com.techsage.banking.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StatisticsControllerTest extends ControllerTestBase {

    @Test
    void stats_Successful() throws Exception {
        mockMvc.perform(get("/statistics")
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerCount").exists())
                .andExpect(jsonPath("$.transactionCount").exists())
                .andExpect(jsonPath("$.totalBankCapital").exists())
                .andExpect(jsonPath("$.transactionTypeChart").exists())
                .andExpect(jsonPath("$.transactionTodayChart").exists());
    }

    @Test
    void stats_ChartDataStructure() throws Exception {
        mockMvc.perform(get("/statistics")
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionTypeChart.labels").exists())
                .andExpect(jsonPath("$.transactionTypeChart.datasets").exists())
                .andExpect(jsonPath("$.transactionTodayChart.labels").exists())
                .andExpect(jsonPath("$.transactionTodayChart.datasets").exists());
    }

    @Test
    void stats_Unauthorized() throws Exception {
        mockMvc.perform(get("/statistics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void stats_ForbiddenForCustomer() throws Exception {
        mockMvc.perform(get("/statistics")
                        .with(authorized(AuthMethod.CUSTOMER)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void stats_NumericFieldsAreCorrectTypes() throws Exception {
        mockMvc.perform(get("/statistics")
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerCount").isNumber())
                .andExpect(jsonPath("$.transactionCount").isNumber())
                .andExpect(jsonPath("$.totalBankCapital").isNumber());
    }

    @Test
    void stats_ChartFieldsAreArrays() throws Exception {
        mockMvc.perform(get("/statistics")
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionTypeChart.labels").isArray())
                .andExpect(jsonPath("$.transactionTypeChart.datasets").isArray())
                .andExpect(jsonPath("$.transactionTodayChart.labels").isArray())
                .andExpect(jsonPath("$.transactionTodayChart.datasets").isArray());
    }
}