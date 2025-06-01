package com.techsage.banking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.enums.UserRole;
import com.techsage.banking.models.enums.UserStatus;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends ControllerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void me_Successful() throws Exception {
        mockMvc.perform(get("/users/me")
                        .with(authorized(AuthMethod.CUSTOMER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.phoneNumber").exists())
                .andExpect(jsonPath("$.roles").exists())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void me_Unauthorized() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAll_Successful() throws Exception {
        mockMvc.perform(get("/users")
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalRecords").exists())
                .andExpect(jsonPath("$.recordsPerPage").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    void getAll_WithPagination() throws Exception {
        mockMvc.perform(get("/users")
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .param("status", "ACTIVE")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.recordsPerPage").value(10));
    }

    @Test
    void getAll_DefaultStatus() throws Exception {
        mockMvc.perform(get("/users")
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAll_Unauthorized() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAll_ForbiddenForCustomer() throws Exception {
        mockMvc.perform(get("/users")
                        .with(authorized(AuthMethod.CUSTOMER)))
                .andExpect(status().isForbidden());
    }

    @Test
    void softDeleteUser_Successful() throws Exception {
        mockMvc.perform(delete("/users/2/softDelete")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELETED"));
    }

    @Test
    void softDeleteUser_UserNotFound() throws Exception {
        mockMvc.perform(delete("/users/99999/softDelete")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void softDeleteUser_Unauthorized() throws Exception {
        mockMvc.perform(delete("/users/2/softDelete")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getById_Successful() throws Exception {
        mockMvc.perform(get("/users/1")
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void getById_UserNotFound() throws Exception {
        mockMvc.perform(get("/users/99999")
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getById_Unauthorized() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void approveUser_Successful() throws Exception {
        ApprovalRequestDto approvalRequest = new ApprovalRequestDto();
        approvalRequest.setTransferLimit(new BigDecimal("1000.00"));
        approvalRequest.setDailyTransferLimit(new BigDecimal("500.00"));
        approvalRequest.setAbsoluteLimitChecking(new BigDecimal("-100.00"));
        approvalRequest.setAbsoluteLimitSavings(new BigDecimal("0.00"));

        mockMvc.perform(post("/users/2/approve")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approvalRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.transferLimit").exists())
                .andExpect(jsonPath("$.dailyLimit").exists());
    }

    @Test
    void approveUser_ValidationError() throws Exception {
        ApprovalRequestDto approvalRequest = new ApprovalRequestDto();
        approvalRequest.setTransferLimit(new BigDecimal("-100.00"));
        approvalRequest.setDailyTransferLimit(new BigDecimal("-50.00"));

        mockMvc.perform(post("/users/2/approve")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approvalRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.transferLimit").exists())
                .andExpect(jsonPath("$.dailyTransferLimit").exists());
    }

    @Test
    void approveUser_DailyLimitExceedsTransferLimit() throws Exception {
        ApprovalRequestDto approvalRequest = new ApprovalRequestDto();
        approvalRequest.setTransferLimit(new BigDecimal("500.00"));
        approvalRequest.setDailyTransferLimit(new BigDecimal("1000.00"));
        approvalRequest.setAbsoluteLimitChecking(new BigDecimal("-100.00"));
        approvalRequest.setAbsoluteLimitSavings(new BigDecimal("0.00"));

        mockMvc.perform(post("/users/2/approve")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approvalRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveUser_UserNotFound() throws Exception {
        ApprovalRequestDto approvalRequest = new ApprovalRequestDto();
        approvalRequest.setTransferLimit(new BigDecimal("1000.00"));
        approvalRequest.setDailyTransferLimit(new BigDecimal("500.00"));
        approvalRequest.setAbsoluteLimitChecking(new BigDecimal("-100.00"));
        approvalRequest.setAbsoluteLimitSavings(new BigDecimal("0.00"));

        mockMvc.perform(post("/users/99999/approve")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approvalRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void reinstateUser_Successful() throws Exception {
        mockMvc.perform(put("/users/2/reinstate")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void reinstateUser_UserNotFound() throws Exception {
        mockMvc.perform(put("/users/99999/reinstate")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateUser_Successful() throws Exception {
        UpdateUserRequestDto updateRequest = new UpdateUserRequestDto();
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("User");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPhoneNumber("+31612345679");
        updateRequest.setBsn("123456789");
        updateRequest.setRoles(Arrays.asList(UserRole.ROLE_USER, UserRole.ROLE_CUSTOMER));
        updateRequest.setDailyLimit(750.0);
        updateRequest.setTransferLimit(1500.0);
        updateRequest.setStatus(UserStatus.ACTIVE);

        mockMvc.perform(put("/users/2/update")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void updateUser_ValidationError() throws Exception {
        UpdateUserRequestDto updateRequest = new UpdateUserRequestDto();
        updateRequest.setFirstName("");
        updateRequest.setLastName("");
        updateRequest.setEmail("invalid-email");
        updateRequest.setBsn("invalid-bsn");

        mockMvc.perform(put("/users/2/update")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.bsn").exists());
    }

    @Test
    void updateSelf_Successful() throws Exception {
        UpdateSelfRequestDto updateRequest = new UpdateSelfRequestDto();
        updateRequest.setEmail("newemail@example.com");
        updateRequest.setPhoneNumber("+31612345680");

        mockMvc.perform(put("/users/me")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newemail@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+31612345680"));
    }

    @Test
    void updateSelf_ValidationError() throws Exception {
        UpdateSelfRequestDto updateRequest = new UpdateSelfRequestDto();
        updateRequest.setEmail("invalid-email");
        updateRequest.setPhoneNumber("");

        mockMvc.perform(put("/users/me")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.phoneNumber").exists());
    }

    @Test
    void updateSelf_Unauthorized() throws Exception {
        UpdateSelfRequestDto updateRequest = new UpdateSelfRequestDto();
        updateRequest.setEmail("newemail@example.com");
        updateRequest.setPhoneNumber("+31612345680");

        mockMvc.perform(put("/users/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePassword_Successful() throws Exception {
        UpdateUserPasswordRequestDto updateRequest = new UpdateUserPasswordRequestDto();
        updateRequest.setNewPassword("NewPassword123!");
        updateRequest.setConfirmNewPassword("NewPassword123!");

        mockMvc.perform(put("/users/2/updatePassword")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePassword_ValidationError() throws Exception {
        UpdateUserPasswordRequestDto updateRequest = new UpdateUserPasswordRequestDto();
        updateRequest.setNewPassword("weak");
        updateRequest.setConfirmNewPassword("different");

        mockMvc.perform(put("/users/2/updatePassword")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.newPassword").exists());
    }

    @Test
    void updatePassword_PasswordsMustMatch() throws Exception {
        UpdateUserPasswordRequestDto updateRequest = new UpdateUserPasswordRequestDto();
        updateRequest.setNewPassword("ValidPassword123!");
        updateRequest.setConfirmNewPassword("DifferentPassword123!");

        mockMvc.perform(put("/users/2/updatePassword")
                        .with(csrf())
                        .with(authorized(AuthMethod.EMPLOYEE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOwnPassword_Successful() throws Exception {
        UpdatePasswordRequestDto updateRequest = new UpdatePasswordRequestDto();
        updateRequest.setCurrentPassword("password123");
        updateRequest.setNewPassword("NewPassword123!");
        updateRequest.setConfirmNewPassword("NewPassword123!");

        mockMvc.perform(put("/users/me/updatePassword")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void updateOwnPassword_WrongCurrentPassword() throws Exception {
        UpdatePasswordRequestDto updateRequest = new UpdatePasswordRequestDto();
        updateRequest.setCurrentPassword("wrongpassword");
        updateRequest.setNewPassword("NewPassword123!");
        updateRequest.setConfirmNewPassword("NewPassword123!");

        mockMvc.perform(put("/users/me/updatePassword")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateOwnPassword_ValidationError() throws Exception {
        UpdatePasswordRequestDto updateRequest = new UpdatePasswordRequestDto();
        updateRequest.setCurrentPassword("");
        updateRequest.setNewPassword("weak");
        updateRequest.setConfirmNewPassword("different");

        mockMvc.perform(put("/users/me/updatePassword")
                        .with(csrf())
                        .with(authorized(AuthMethod.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.currentPassword").exists())
                .andExpect(jsonPath("$.newPassword").exists());
    }

//    @Test
//    void updateOwnPassword_Unauthorized() throws Exception {
//        UpdatePasswordRequestDto updateRequest = new UpdatePasswordRequestDto();
//        updateRequest.setCurrentPassword("password123");
//        updateRequest.setNewPassword("NewPassword123!");
//        updateRequest.setConfirmNewPassword("NewPassword123!");
//
//        mockMvc.perform(put("/users/me/updatePassword")
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper
}