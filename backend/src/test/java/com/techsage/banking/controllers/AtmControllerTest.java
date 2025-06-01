package com.techsage.banking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.requests.AtmDepositDto;
import com.techsage.banking.models.dto.requests.AtmWithdrawDto;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.repositories.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AtmControllerTest extends ControllerTestBase {

    private List<BankAccount> checkingAccounts;

    @BeforeEach
    void setUp() {
        User testCustomer = userRepository.getByEmail("johncustomer@example.com").orElse(null);
        checkingAccounts = bankAccountRepository.findByUserAndType(testCustomer, BankAccountType.CHECKING);
    }

    @Test
    void deposit_Successful() throws Exception {
        AtmDepositDto depositRequest = new AtmDepositDto();
        depositRequest.setDepositTo(checkingAccounts.getFirst().getIban().toString());
        depositRequest.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/atm/deposit")
                        .with(csrf())
                        .with(authorized(AuthMethod.ATM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").exists())
                .andExpect(jsonPath("$.iban").exists())
                .andExpect(jsonPath("$.type").exists());
    }

    @Test
    void deposit_InvalidIban() throws Exception {
        AtmDepositDto depositRequest = new AtmDepositDto();
        depositRequest.setDepositTo("INVALID_IBAN");
        depositRequest.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/atm/deposit")
                        .with(csrf())
                        .with(authorized(AuthMethod.ATM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.depositTo").value("Invalid Iban"));
    }

    @Test
    void deposit_BankAccountNotFound() throws Exception {
        AtmDepositDto depositRequest = new AtmDepositDto();
        depositRequest.setDepositTo("NL36INGB2749559820");
        depositRequest.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/atm/deposit")
                        .with(csrf())
                        .with(authorized(AuthMethod.ATM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deposit_ValidationError() throws Exception {
        AtmDepositDto depositRequest = new AtmDepositDto();
        depositRequest.setDepositTo("");
        depositRequest.setAmount(new BigDecimal("0.00"));

        mockMvc.perform(post("/atm/deposit")
                        .with(csrf())
                        .with(authorized(AuthMethod.ATM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.depositTo").exists())
                .andExpect(jsonPath("$.amount").exists());
    }

    @Test
    void deposit_NullAmount() throws Exception {
        AtmDepositDto depositRequest = new AtmDepositDto();
        depositRequest.setDepositTo(checkingAccounts.getFirst().getIban().toString());
        depositRequest.setAmount(null);

        mockMvc.perform(post("/atm/deposit")
                        .with(csrf())
                        .with(authorized(AuthMethod.ATM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").exists());
    }

    @Test
    void deposit_NegativeAmount() throws Exception {
        AtmDepositDto depositRequest = new AtmDepositDto();
        depositRequest.setDepositTo(checkingAccounts.getFirst().getIban().toString());
        depositRequest.setAmount(new BigDecimal("-100.00"));

        mockMvc.perform(post("/atm/deposit")
                        .with(csrf())
                        .with(authorized(AuthMethod.ATM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").exists());
    }

    @Test
    void deposit_UnAuthorized() throws Exception {
        AtmDepositDto depositRequest = new AtmDepositDto();
        depositRequest.setDepositTo(checkingAccounts.getFirst().getIban().toString());
        depositRequest.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/atm/deposit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void withdraw_Successful() throws Exception {
        AtmWithdrawDto withdrawRequest = new AtmWithdrawDto();
        withdrawRequest.setWithdrawFrom(checkingAccounts.getFirst().getIban().toString());
        withdrawRequest.setAmount(new BigDecimal("50.00"));

        mockMvc.perform(post("/atm/withdraw")
                        .with(csrf())
                        .with(authorized(AuthMethod.ATM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").exists())
                .andExpect(jsonPath("$.iban").exists())
                .andExpect(jsonPath("$.type").exists());
    }

    @Test
    void withdraw_InvalidIban() throws Exception {
        AtmWithdrawDto withdrawRequest = new AtmWithdrawDto();
        withdrawRequest.setWithdrawFrom("INVALID_IBAN");
        withdrawRequest.setAmount(new BigDecimal("50.00"));

        mockMvc.perform(post("/atm/withdraw")
                        .with(csrf())
                        .with(authorized(AuthMethod.ATM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.withdrawFrom").value("Invalid Iban"));
    }

    @Test
    void withdraw_BankAccountNotFound() throws Exception {
        AtmWithdrawDto withdrawRequest = new AtmWithdrawDto();
        withdrawRequest.setWithdrawFrom("NL36INGB2749559820");
        withdrawRequest.setAmount(new BigDecimal("50.00"));

        mockMvc.perform(post("/atm/withdraw")
                        .with(csrf())
                        .with(authorized(AuthMethod.ATM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void withdraw_InsufficientFunds() throws Exception {
        AtmWithdrawDto withdrawRequest = new AtmWithdrawDto();
        withdrawRequest.setWithdrawFrom(checkingAccounts.getFirst().getIban().toString());
        withdrawRequest.setAmount(new BigDecimal("999999.00"));

        mockMvc.perform(post("/atm/withdraw")
                        .with(csrf())
                        .with(authorized(AuthMethod.ATM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void withdraw_ValidationError() throws Exception {
        AtmWithdrawDto withdrawRequest = new AtmWithdrawDto();
        withdrawRequest.setWithdrawFrom("");
        withdrawRequest.setAmount(new BigDecimal("0.00"));

        mockMvc.perform(post("/atm/withdraw")
                        .with(csrf())
                        .with(authorized(AuthMethod.ATM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.withdrawFrom").exists())
                .andExpect(jsonPath("$.amount").exists());
    }

    @Test
    void withdraw_NullAmount() throws Exception {
        AtmWithdrawDto withdrawRequest = new AtmWithdrawDto();
        withdrawRequest.setWithdrawFrom(checkingAccounts.getFirst().getIban().toString());
        withdrawRequest.setAmount(null);

        mockMvc.perform(post("/atm/withdraw")
                        .with(csrf())
                        .with(authorized(AuthMethod.ATM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").exists());
    }

    @Test
    void withdraw_NegativeAmount() throws Exception {
        AtmWithdrawDto withdrawRequest = new AtmWithdrawDto();
        withdrawRequest.setWithdrawFrom(checkingAccounts.getFirst().getIban().toString());
        withdrawRequest.setAmount(new BigDecimal("-50.00"));

        mockMvc.perform(post("/atm/withdraw")
                        .with(csrf())
                        .with(authorized(AuthMethod.ATM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").exists());
    }

    @Test
    void withdraw_UnAuthorized() throws Exception {
        AtmWithdrawDto withdrawRequest = new AtmWithdrawDto();
        withdrawRequest.setWithdrawFrom(checkingAccounts.getFirst().getIban().toString());
        withdrawRequest.setAmount(new BigDecimal("50.00"));

        mockMvc.perform(post("/atm/withdraw")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isUnauthorized());
    }
}