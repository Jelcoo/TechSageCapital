package com.techsage.banking.controllers;

import com.techsage.banking.models.dto.requests.UpdateAbsoluteMinimumBalanceRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BankAccountControllerTest extends ControllerTestBase {

    @Test
    void getBankAccounts_Successful() throws Exception {
        mockMvc.perform(get("/bankAccounts")
                        .with(authorized(AuthMethod.CUSTOMER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalRecords").exists())
                .andExpect(jsonPath("$.recordsPerPage").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    void getBankAccounts_WithTypeFilter() throws Exception {
        mockMvc.perform(get("/bankAccounts")
                        .with(authorized(AuthMethod.CUSTOMER))
                        .param("type", "CHECKING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getBankAccounts_WithPagination() throws Exception {
        mockMvc.perform(get("/bankAccounts")
                        .with(authorized(AuthMethod.CUSTOMER))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.recordsPerPage").value(10));
    }

    @Test
    void getBankAccounts_Unauthorized() throws Exception {
        mockMvc.perform(get("/bankAccounts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getBankAccounts_ForbiddenForEmployee() throws Exception {
        mockMvc.perform(get("/bankAccounts")
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findByName_Successful() throws Exception {
        mockMvc.perform(get("/bankAccounts/find")
                        .with(authorized(AuthMethod.CUSTOMER))
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalRecords").exists())
                .andExpect(jsonPath("$.recordsPerPage").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    void findByName_WithPagination() throws Exception {
        mockMvc.perform(get("/bankAccounts/find")
                        .with(authorized(AuthMethod.CUSTOMER))
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.recordsPerPage").value(5));
    }

    @Test
    void findByName_Unauthorized() throws Exception {
        mockMvc.perform(get("/bankAccounts/find")
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findByName_AccessibleByEmployee() throws Exception {
        mockMvc.perform(get("/bankAccounts/find")
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void updateAbsoluteMinimumBalance_Successful() throws Exception {
        UpdateAbsoluteMinimumBalanceRequestDto request = new UpdateAbsoluteMinimumBalanceRequestDto();
        request.setAbsoluteMinimumBalance(new BigDecimal("-500.00"));

        mockMvc.perform(put("/bankAccounts/1/absoluteLimit")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateAbsoluteMinimumBalance_ValidationError() throws Exception {
        UpdateAbsoluteMinimumBalanceRequestDto request = new UpdateAbsoluteMinimumBalanceRequestDto();
        request.setAbsoluteMinimumBalance(null);

        mockMvc.perform(put("/bankAccounts/1/absoluteLimit")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAbsoluteMinimumBalance_BankAccountNotFound() throws Exception {
        UpdateAbsoluteMinimumBalanceRequestDto request = new UpdateAbsoluteMinimumBalanceRequestDto();
        request.setAbsoluteMinimumBalance(new BigDecimal("-500.00"));

        mockMvc.perform(put("/bankAccounts/99999/absoluteLimit")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateAbsoluteMinimumBalance_Unauthorized() throws Exception {
        UpdateAbsoluteMinimumBalanceRequestDto request = new UpdateAbsoluteMinimumBalanceRequestDto();
        request.setAbsoluteMinimumBalance(new BigDecimal("-500.00"));

        mockMvc.perform(put("/bankAccounts/1/absoluteLimit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateAbsoluteMinimumBalance_ForbiddenForCustomer() throws Exception {
        UpdateAbsoluteMinimumBalanceRequestDto request = new UpdateAbsoluteMinimumBalanceRequestDto();
        request.setAbsoluteMinimumBalance(new BigDecimal("-500.00"));

        mockMvc.perform(put("/bankAccounts/1/absoluteLimit")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
