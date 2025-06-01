package com.techsage.banking.services;

import com.techsage.banking.helpers.IbanHelper;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.enums.BankAccountType;
import com.techsage.banking.models.info.BankAccountInfoWithoutBalance;
import com.techsage.banking.repositories.BankAccountRepository;
import com.techsage.banking.services.interfaces.BankAccountService;
import org.iban4j.Iban;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.*;

@Service
public class BankAccountServiceJpa implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final ModelMapper modelMapper;

    public BankAccountServiceJpa(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(BankAccount.class, BankAccountInfoWithoutBalance.class).addMappings(mapper -> {
            mapper.map(src -> src.getUser().getFirstName(), BankAccountInfoWithoutBalance::setFirstName);
            mapper.map(src -> src.getUser().getLastName(), BankAccountInfoWithoutBalance::setLastName);
            mapper.map(src -> src.getIban() == null ? "" : src.getIban().getAccountNumber(), BankAccountInfoWithoutBalance::setIban);
            mapper.map(BankAccount::getType, BankAccountInfoWithoutBalance::setType);
        });
    }

    @Override
    public Page<BankAccountDto> findByUserAndType(User user, BankAccountType type, Pageable pageable) {
        Page<BankAccount> bankAccountsPage;
        if (type == null) {
            bankAccountsPage = bankAccountRepository.findByUser(user, pageable);
        } else {
            bankAccountsPage = bankAccountRepository.findByUserAndType(user, type, pageable);
        }
        return bankAccountsPage.map(account -> modelMapper.map(account, BankAccountDto.class));
    }

    @Override
    public BankAccount getByIban(Iban iban) {
        return bankAccountRepository.findByIban(iban);
    }

    @Override
    public Page<BankAccountInfoWithoutBalance> findByFirstNameAndLastName(String firstName, String lastName, Pageable pageable) {
        Page<BankAccount> bankAccountsPage = bankAccountRepository
                .findByUserFirstNameStartingWithIgnoreCaseAndUserLastNameStartingWithIgnoreCaseAndType(firstName, lastName, BankAccountType.CHECKING, pageable);

        return bankAccountsPage.map(account -> modelMapper.map(account, BankAccountInfoWithoutBalance.class));
    }

    @Override
    public BankAccount create(User user, BankAccountType bankAccountType, BigDecimal absoluteMinimumBalance, BigDecimal balance) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban(generateUniqueIban());
        bankAccount.setUser(user);
        bankAccount.setType(bankAccountType);
        bankAccount.setAbsoluteMinimumBalance(absoluteMinimumBalance);
        bankAccount.setBalance(balance);

        return bankAccountRepository.save(bankAccount);
    }

    private Iban generateUniqueIban() {
        Iban iban;
        do {
            iban = IbanHelper.generateIban();
        } while (getByIban(iban) != null);
        return iban;
    }

    @Override
    public BankAccount update(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public BankAccount getById(long id) {
        return bankAccountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Bank account not found"));
    }

    @Override
    public BankAccountDto updateAbsoluteMinimumBalance(long id, BigDecimal absoluteMinimumBalance) {
        BankAccount bankAccount = this.getById(id);
        bankAccount.setAbsoluteMinimumBalance(absoluteMinimumBalance);
        return modelMapper.map(bankAccountRepository.save(bankAccount), BankAccountDto.class);
    }
}
