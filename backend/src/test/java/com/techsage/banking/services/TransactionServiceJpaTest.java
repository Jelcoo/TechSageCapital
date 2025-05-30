package com.techsage.banking.services;

import com.techsage.banking.exceptions.TransactionException;
import com.techsage.banking.helpers.TransactionHelper;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.TransactionDto;
import com.techsage.banking.models.dto.requests.TransactionFilterRequestDto;
import com.techsage.banking.models.dto.requests.TransactionRequestDto;
import com.techsage.banking.models.enums.TransactionType;
import com.techsage.banking.repositories.TransactionRepository;
import com.techsage.banking.services.interfaces.BankAccountService;
import com.techsage.banking.services.interfaces.UserService;
import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TransactionServiceJpaTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private TransactionHelper transactionHelper;
    @Mock private BankAccountService bankAccountService;
    @Mock private UserService userService;

    @InjectMocks private TransactionServiceJpa transactionServiceJpa;

    private User user;
    private BankAccount fromAccount;
    private BankAccount toAccount;
    private TransactionRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@example.com");

        fromAccount = new BankAccount();
        fromAccount.setId(1L);
        fromAccount.setUser(user);
        fromAccount.setBalance(BigDecimal.valueOf(1000));

        toAccount = new BankAccount();
        toAccount.setId(2L);
        toAccount.setBalance(BigDecimal.valueOf(500));

        requestDto = new TransactionRequestDto();
        requestDto.setFromIban(Iban.random().toString());
        requestDto.setToIban(Iban.random().toString());
        requestDto.setAmount(BigDecimal.valueOf(100));
        requestDto.setDescription("Test transaction");
    }

    @Test
    void testGetAllTransactions() {
        Pageable pageable = PageRequest.of(0, 5);
        Transaction transaction = new Transaction();
        when(transactionRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(new PageImpl<>(List.of(transaction)));

        Page<TransactionDto> result = transactionServiceJpa.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(transactionRepository).findAllByOrderByCreatedAtDesc(pageable);
    }

    @Test
    void testGetByAccountId_WhenAccountExists() {
        Pageable pageable = PageRequest.of(0, 5);
        TransactionFilterRequestDto filterRequestDto = new TransactionFilterRequestDto();
        when(bankAccountService.getById(1L)).thenReturn(fromAccount);
        when(transactionRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findAllByBankAccount(fromAccount, pageable, filterRequestDto))
                .thenReturn(new PageImpl<>(List.of(new Transaction())));

        Page<TransactionDto> result = transactionServiceJpa.getByAccountId(1L, pageable, filterRequestDto);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetByAccountId_WhenAccountNotExists_ThrowsException() {
        TransactionFilterRequestDto filterRequestDto = new TransactionFilterRequestDto();
        when(bankAccountService.getById(1L)).thenReturn(fromAccount);
        when(transactionRepository.existsById(1L)).thenReturn(false);

        assertThrows(TransactionException.class, () -> transactionServiceJpa.getByAccountId(1L, PageRequest.of(0, 5), filterRequestDto));
    }

    @Test
    void testGetByAccountIdAndCustomer_ValidRequest() {
        TransactionFilterRequestDto filterRequestDto = new TransactionFilterRequestDto();
        when(userService.getByEmailRaw("user@example.com")).thenReturn(user);
        when(bankAccountService.getById(1L)).thenReturn(fromAccount);
        when(transactionRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findAllByBankAccount(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Transaction())));

        Page<TransactionDto> result = transactionServiceJpa.getByAccountIdAndCustomer(1L, "user@example.com", PageRequest.of(0, 5), filterRequestDto);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetByAccountIdAndCustomer_InvalidUser_ThrowsException() {
        User anotherUser = new User();
        anotherUser.setEmail("other@example.com");

        TransactionFilterRequestDto filterRequestDto = new TransactionFilterRequestDto();
        when(userService.getByEmailRaw("user@example.com")).thenReturn(anotherUser);
        when(bankAccountService.getById(1L)).thenReturn(fromAccount);

        assertThrows(TransactionException.class,
                () -> transactionServiceJpa.getByAccountIdAndCustomer(1L, "user@example.com", PageRequest.of(0, 5), filterRequestDto));
    }

    @Test
    void testCreateTransaction_Success() {
        Iban fromIban = Iban.valueOf(requestDto.getFromIban());
        Iban toIban = Iban.valueOf(requestDto.getToIban());

        when(bankAccountService.getByIban(fromIban)).thenReturn(fromAccount);
        when(bankAccountService.getByIban(toIban)).thenReturn(toAccount);
        doNothing().when(transactionHelper).validateTransaction(any(), any(), any(), any());

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bankAccountService.update(any(BankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionDto result = transactionServiceJpa.create(requestDto, user);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(900), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(600), toAccount.getBalance());
    }

    @Test
    void testCreateTransaction_ValidationFails_ThrowsException() throws TransactionException {
        Iban fromIban = Iban.valueOf(requestDto.getFromIban());
        Iban toIban = Iban.valueOf(requestDto.getToIban());

        when(bankAccountService.getByIban(fromIban)).thenReturn(fromAccount);
        when(bankAccountService.getByIban(toIban)).thenReturn(toAccount);
        doThrow(new TransactionException(TransactionException.Reason.CHECK_OWNERSHIP))
                .when(transactionHelper).validateTransaction(fromAccount, toAccount, user, requestDto.getAmount());

        assertThrows(TransactionException.class, () -> transactionServiceJpa.create(requestDto, user));
    }

    @Test
    void testCreateTransaction_SaveFails_ThrowsException() {
        Iban fromIban = Iban.valueOf(requestDto.getFromIban());
        Iban toIban = Iban.valueOf(requestDto.getToIban());

        when(bankAccountService.getByIban(fromIban)).thenReturn(fromAccount);
        when(bankAccountService.getByIban(toIban)).thenReturn(toAccount);
        doNothing().when(transactionHelper).validateTransaction(any(), any(), any(), any());

        when(bankAccountService.update(any())).thenReturn(fromAccount);
        doThrow(RuntimeException.class).when(transactionRepository).save(any(Transaction.class));

        assertThrows(TransactionException.class, () -> transactionServiceJpa.create(requestDto, user));
    }

    @Test
    void testFindSumOfTransactionsByFromAccount() {
        LocalDateTime date = LocalDateTime.now();
        when(transactionRepository.findSumOfTransactionsByFromAccount(fromAccount, date))
                .thenReturn(BigDecimal.valueOf(300));

        BigDecimal result = transactionServiceJpa.findSumOfTransactionsByFromAccount(fromAccount, date);

        assertEquals(BigDecimal.valueOf(300), result);
        verify(transactionRepository).findSumOfTransactionsByFromAccount(fromAccount, date);
    }
}
