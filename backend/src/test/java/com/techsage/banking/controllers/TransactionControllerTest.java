package com.techsage.banking.controllers;

import com.techsage.banking.models.*;
import com.techsage.banking.models.dto.requests.TransactionRequestDto;
import com.techsage.banking.models.enums.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionControllerTest extends ControllerTestBase {

    private List<BankAccount> johnCheckingAccounts;
    private List<BankAccount> emmaCheckingAccounts;

    @BeforeEach
    void setUp() {
        User johnCustomer = userRepository.getByEmail("johncustomer@example.com").orElse(null);
        User emmaCustomer = userRepository.getByEmail("emmacustomer@example.com").orElse(null);
        johnCheckingAccounts = bankAccountRepository.findByUserAndType(johnCustomer, BankAccountType.CHECKING);
        emmaCheckingAccounts = bankAccountRepository.findByUserAndType(emmaCustomer, BankAccountType.CHECKING);
    }

    @Test
    void getTransactionsForId_Successful() throws Exception {
        mockMvc.perform(get("/transactions/1")
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalRecords").exists())
                .andExpect(jsonPath("$.recordsPerPage").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    void getTransactionsForId_WithPagination() throws Exception {
        mockMvc.perform(get("/transactions/1")
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.recordsPerPage").value(5));
    }

    @Test
    void getTransactionsForId_Unauthorized() throws Exception {
        mockMvc.perform(get("/transactions/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTransactionsForId_ForbiddenForCustomer() throws Exception {
        mockMvc.perform(get("/transactions/1")
                        .with(authorized(AuthMethod.CUSTOMER)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCustomerTransactionsForId_Successful() throws Exception {
        mockMvc.perform(get("/transactions/1/me")
                        .with(authorized(AuthMethod.CUSTOMER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalRecords").exists())
                .andExpect(jsonPath("$.recordsPerPage").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    void getCustomerTransactionsForId_WithPagination() throws Exception {
        mockMvc.perform(get("/transactions/1/me")
                        .with(authorized(AuthMethod.CUSTOMER))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.recordsPerPage").value(10));
    }

    @Test
    void getCustomerTransactionsForId_Unauthorized() throws Exception {
        mockMvc.perform(get("/transactions/1/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllTransactions_Successful() throws Exception {
        mockMvc.perform(get("/transactions")
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalRecords").exists())
                .andExpect(jsonPath("$.recordsPerPage").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    void getAllTransactions_WithPagination() throws Exception {
        mockMvc.perform(get("/transactions")
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.recordsPerPage").value(20));
    }

    @Test
    void getAllTransactions_Unauthorized() throws Exception {
        mockMvc.perform(get("/transactions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllTransactions_ForbiddenForCustomer() throws Exception {
        mockMvc.perform(get("/transactions")
                        .with(authorized(AuthMethod.CUSTOMER)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void createTransaction_Successful() throws Exception {
        TransactionRequestDto transactionRequest = new TransactionRequestDto();
        transactionRequest.setFromIban(johnCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setToIban(emmaCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setAmount(new BigDecimal("100.50"));
        transactionRequest.setDescription("Test transaction");

        mockMvc.perform(post("/transactions/create")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").exists())
                .andExpect(jsonPath("$.description").value("Test transaction"));
    }

    @Test
    @Transactional
    void createTransaction_SuccessfulAsEmployee() throws Exception {
        TransactionRequestDto transactionRequest = new TransactionRequestDto();
        transactionRequest.setFromIban(johnCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setToIban(emmaCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setAmount(new BigDecimal("250.75"));
        transactionRequest.setDescription("Employee transaction");

        mockMvc.perform(post("/transactions/create")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").exists())
                .andExpect(jsonPath("$.description").value("Employee transaction"));
    }

    @Test
    void createTransaction_ValidationError_NullAmount() throws Exception {
        TransactionRequestDto transactionRequest = new TransactionRequestDto();
        transactionRequest.setFromIban(johnCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setToIban(emmaCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setAmount(null);
        transactionRequest.setDescription("Test transaction");

        mockMvc.perform(post("/transactions/create")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").exists());
    }

    @Test
    void createTransaction_ValidationError_NegativeAmount() throws Exception {
        TransactionRequestDto transactionRequest = new TransactionRequestDto();
        transactionRequest.setFromIban(johnCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setToIban(emmaCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setAmount(new BigDecimal("-50.00"));
        transactionRequest.setDescription("Test transaction");

        mockMvc.perform(post("/transactions/create")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").exists());
    }

    @Test
    void createTransaction_ValidationError_ZeroAmount() throws Exception {
        TransactionRequestDto transactionRequest = new TransactionRequestDto();
        transactionRequest.setFromIban(johnCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setToIban(emmaCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setAmount(new BigDecimal("0.00"));
        transactionRequest.setDescription("Test transaction");

        mockMvc.perform(post("/transactions/create")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").exists());
    }

    @Test
    void createTransaction_ValidationError_InvalidIban() throws Exception {
        TransactionRequestDto transactionRequest = new TransactionRequestDto();
        transactionRequest.setFromIban("INVALIDIBAN");
        transactionRequest.setToIban("INVALIDIBAN");
        transactionRequest.setAmount(new BigDecimal("100.00"));
        transactionRequest.setDescription("Test transaction");

        mockMvc.perform(post("/transactions/create")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.toIban").value("Invalid Iban"))
                .andExpect(jsonPath("$.fromIban").value("Invalid Iban"));
    }

    @Test
    void createTransaction_BankAccountNotFound() throws Exception {
        TransactionRequestDto transactionRequest = new TransactionRequestDto();
        transactionRequest.setFromIban("NL36INGB2749559820");
        transactionRequest.setToIban(emmaCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setAmount(new BigDecimal("100.00"));
        transactionRequest.setDescription("Test transaction");

        mockMvc.perform(post("/transactions/create")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createTransaction_InsufficientFunds() throws Exception {
        TransactionRequestDto transactionRequest = new TransactionRequestDto();
        transactionRequest.setFromIban(johnCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setToIban(emmaCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setAmount(new BigDecimal("999999.99"));
        transactionRequest.setDescription("Large transaction");

        mockMvc.perform(post("/transactions/create")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createTransaction_Unauthorized() throws Exception {
        TransactionRequestDto transactionRequest = new TransactionRequestDto();
        transactionRequest.setFromIban(johnCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setToIban(emmaCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setAmount(new BigDecimal("100.00"));
        transactionRequest.setDescription("Test transaction");

        mockMvc.perform(post("/transactions/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createTransaction_MinimalValidAmount() throws Exception {
        TransactionRequestDto transactionRequest = new TransactionRequestDto();
        transactionRequest.setFromIban(johnCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setToIban(emmaCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setAmount(new BigDecimal("0.01"));
        transactionRequest.setDescription("Minimal transaction");

        mockMvc.perform(post("/transactions/create")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").exists())
                .andExpect(jsonPath("$.description").value("Minimal transaction"));
    }

    @Test
    void createTransaction_SameAccount() throws Exception {
        TransactionRequestDto transactionRequest = new TransactionRequestDto();
        transactionRequest.setFromIban(johnCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setToIban(johnCheckingAccounts.getFirst().getIban().toString());
        transactionRequest.setAmount(new BigDecimal("100.00"));
        transactionRequest.setDescription("Same account transaction");

        mockMvc.perform(post("/transactions/create")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }
}
