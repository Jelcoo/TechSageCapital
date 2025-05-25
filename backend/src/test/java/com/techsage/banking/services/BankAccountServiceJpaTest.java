package com.techsage.banking.services;

import com.techsage.banking.helpers.IbanHelper;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.BankAccountDto;
import com.techsage.banking.models.enums.BankAccountType;
import com.techsage.banking.models.info.BankAccountInfoWithoutBalance;
import com.techsage.banking.repositories.BankAccountRepository;
import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class BankAccountServiceJpaTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountServiceJpa bankAccountServiceJpa;

    private User user;
    private BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        bankAccount = new BankAccount();
        bankAccount.setId(1L);
        bankAccount.setUser(user);
        bankAccount.setType(BankAccountType.SAVINGS);
        bankAccount.setBalance(BigDecimal.valueOf(1000));
        bankAccount.setAbsoluteMinimumBalance(BigDecimal.ZERO);
    }

    @Test
    void testFindByUserAndType_WhenTypeIsNull() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BankAccount> page = new PageImpl<>(List.of(bankAccount));

        when(bankAccountRepository.findByUser(user, pageable)).thenReturn(page);

        Page<BankAccountDto> result = bankAccountServiceJpa.findByUserAndType(user, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(bankAccountRepository).findByUser(user, pageable);
    }

    @Test
    void testFindByUserAndType_WithType() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BankAccount> page = new PageImpl<>(List.of(bankAccount));

        when(bankAccountRepository.findByUserAndType(user, BankAccountType.SAVINGS, pageable)).thenReturn(page);

        Page<BankAccountDto> result = bankAccountServiceJpa.findByUserAndType(user, BankAccountType.SAVINGS, pageable);

        assertEquals(1, result.getTotalElements());
        verify(bankAccountRepository).findByUserAndType(user, BankAccountType.SAVINGS, pageable);
    }

    @Test
    void testGetByIban() {
        Iban iban = Iban.random();
        bankAccount.setIban(iban);

        when(bankAccountRepository.findByIban(iban)).thenReturn(bankAccount);

        BankAccount result = bankAccountServiceJpa.getByIban(iban);

        assertEquals(bankAccount, result);
        verify(bankAccountRepository).findByIban(iban);
    }

    @Test
    void testFindByFirstNameAndLastName() {
        Pageable pageable = PageRequest.of(0, 10);
        bankAccount.setType(BankAccountType.CHECKING);
        Page<BankAccount> page = new PageImpl<>(List.of(bankAccount));

        when(bankAccountRepository
                .findByUserFirstNameStartingWithIgnoreCaseAndUserLastNameStartingWithIgnoreCaseAndType(
                        "John", "Doe", BankAccountType.CHECKING, pageable)).thenReturn(page);

        Page<BankAccountInfoWithoutBalance> result = bankAccountServiceJpa.findByFirstNameAndLastName("John", "Doe", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testCreateBankAccount() {
        BigDecimal balance = BigDecimal.valueOf(5000);
        BigDecimal minBalance = BigDecimal.valueOf(100);
        Iban iban = Iban.random();

        // Override IbanHelper.generateIban() using mockStatic
        try (MockedStatic<IbanHelper> mocked = mockStatic(IbanHelper.class)) {
            mocked.when(IbanHelper::generateIban).thenReturn(iban);
            when(bankAccountRepository.findByIban(iban)).thenReturn(null);
            when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BankAccount result = bankAccountServiceJpa.create(user, BankAccountType.CHECKING, minBalance, balance);

            assertNotNull(result);
            assertEquals(balance, result.getBalance());
            assertEquals(BankAccountType.CHECKING, result.getType());
            verify(bankAccountRepository).save(any(BankAccount.class));
        }
    }

    @Test
    void testUpdateBankAccount() {
        when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);

        BankAccount result = bankAccountServiceJpa.update(bankAccount);

        assertEquals(bankAccount, result);
        verify(bankAccountRepository).save(bankAccount);
    }

    @Test
    void testGetById_WhenFound() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));

        BankAccount result = bankAccountServiceJpa.getById(1L);

        assertEquals(bankAccount, result);
        verify(bankAccountRepository).findById(1L);
    }

    @Test
    void testGetById_WhenNotFound() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bankAccountServiceJpa.getById(1L));
    }
}
