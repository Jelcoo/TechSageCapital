package com.techsage.banking.services;

import com.techsage.banking.exceptions.*;
import com.techsage.banking.helpers.*;
import com.techsage.banking.models.*;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.repositories.*;
import com.techsage.banking.services.interfaces.*;
import org.iban4j.*;
import org.junit.jupiter.api.*;

import java.math.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AtmServiceJpaTest {

    private BankAccountService bankAccountService;
    private TransactionRepository transactionRepository;
    private TransactionHelper transactionHelper;
    private AtmServiceJpa atmService;

    @BeforeEach
    void setUp() {
        bankAccountService = mock(BankAccountService.class);
        transactionRepository = mock(TransactionRepository.class);
        transactionHelper = mock(TransactionHelper.class);
        atmService = new AtmServiceJpa(bankAccountService, transactionRepository, transactionHelper);
    }

    private TransactionException exception(TransactionException.Reason reason) {
        return new TransactionException(reason);
    }

    @Test
    void deposit_shouldSucceed() {
        User user = new User();
        BankAccount account = new BankAccount();
        account.setIban(Iban.valueOf("DE44500105175407324931"));
        account.setBalance(BigDecimal.valueOf(100));

        AtmDepositDto dto = new AtmDepositDto();
        dto.setDepositTo(account.getIban().toString());
        dto.setAmount(BigDecimal.valueOf(50));

        when(bankAccountService.getByIban(any())).thenReturn(account);
        doNothing().when(transactionHelper).validateAtmDeposit(account, user);
        when(bankAccountService.update(account)).thenReturn(account);
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BankAccountDto result = atmService.deposit(dto, user);

        assertEquals(BigDecimal.valueOf(150), result.getBalance());
        verify(transactionRepository).save(any());
    }

    @Test
    void withdraw_shouldSucceed() {
        User user = new User();
        BankAccount account = new BankAccount();
        account.setIban(Iban.valueOf("DE44500105175407324931"));
        account.setBalance(BigDecimal.valueOf(200));

        AtmWithdrawDto dto = new AtmWithdrawDto();
        dto.setWithdrawFrom(account.getIban().toString());
        dto.setAmount(BigDecimal.valueOf(80));

        when(bankAccountService.getByIban(any())).thenReturn(account);
        doNothing().when(transactionHelper).validateAtmWithdrawal(account, user, dto.getAmount());
        when(bankAccountService.update(account)).thenReturn(account);
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BankAccountDto result = atmService.withdraw(dto, user);

        assertEquals(BigDecimal.valueOf(120), result.getBalance());
        verify(transactionRepository).save(any());
    }

    @Test
    void deposit_shouldThrowValidationException() {
        User user = new User();
        BankAccount account = new BankAccount();
        account.setIban(Iban.valueOf("DE44500105175407324931"));

        AtmDepositDto dto = new AtmDepositDto();
        dto.setDepositTo(account.getIban().toString());
        dto.setAmount(BigDecimal.valueOf(50));

        when(bankAccountService.getByIban(any())).thenReturn(account);
        doThrow(exception(TransactionException.Reason.CHECK_OWNERSHIP))
                .when(transactionHelper).validateAtmDeposit(account, user);

        TransactionException ex = assertThrows(TransactionException.class, () -> atmService.deposit(dto, user));
        assertEquals(TransactionException.Reason.CHECK_OWNERSHIP, ex.getReason());
    }

    @Test
    void withdraw_shouldThrowValidationException() {
        User user = new User();
        BankAccount account = new BankAccount();
        account.setIban(Iban.valueOf("DE44500105175407324931"));

        AtmWithdrawDto dto = new AtmWithdrawDto();
        dto.setWithdrawFrom(account.getIban().toString());
        dto.setAmount(BigDecimal.valueOf(100));

        when(bankAccountService.getByIban(any())).thenReturn(account);
        doThrow(exception(TransactionException.Reason.CHECK_WITHDRAWAL_LIMIT))
                .when(transactionHelper).validateAtmWithdrawal(account, user, dto.getAmount());

        TransactionException ex = assertThrows(TransactionException.class, () -> atmService.withdraw(dto, user));
        assertEquals(TransactionException.Reason.CHECK_WITHDRAWAL_LIMIT, ex.getReason());
    }

    @Test
    void deposit_shouldThrowTransactionFailedExceptionOnUpdate() {
        User user = new User();
        BankAccount account = new BankAccount();
        account.setIban(Iban.valueOf("DE44500105175407324931"));
        account.setBalance(BigDecimal.valueOf(100));

        AtmDepositDto dto = new AtmDepositDto();
        dto.setDepositTo(account.getIban().toString());
        dto.setAmount(BigDecimal.valueOf(50));

        when(bankAccountService.getByIban(any())).thenReturn(account);
        doNothing().when(transactionHelper).validateAtmDeposit(account, user);
        doThrow(new RuntimeException("DB error")).when(bankAccountService).update(account);

        TransactionException ex = assertThrows(TransactionException.class, () -> atmService.deposit(dto, user));
        assertEquals(TransactionException.Reason.TRANSACTION_FAILED, ex.getReason());
    }

    @Test
    void withdraw_shouldThrowTransactionFailedExceptionOnUpdate() {
        User user = new User();
        BankAccount account = new BankAccount();
        account.setIban(Iban.valueOf("DE44500105175407324931"));
        account.setBalance(BigDecimal.valueOf(200));

        AtmWithdrawDto dto = new AtmWithdrawDto();
        dto.setWithdrawFrom(account.getIban().toString());
        dto.setAmount(BigDecimal.valueOf(50));

        when(bankAccountService.getByIban(any())).thenReturn(account);
        doNothing().when(transactionHelper).validateAtmWithdrawal(account, user, dto.getAmount());
        doThrow(new RuntimeException("DB error")).when(bankAccountService).update(account);

        TransactionException ex = assertThrows(TransactionException.class, () -> atmService.withdraw(dto, user));
        assertEquals(TransactionException.Reason.TRANSACTION_FAILED, ex.getReason());
    }
}
