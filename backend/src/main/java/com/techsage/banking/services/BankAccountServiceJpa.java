package com.techsage.banking.services;

import com.techsage.banking.helpers.IbanHelper;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.BankAccountDto;
import com.techsage.banking.models.enums.BankAccountType;
import com.techsage.banking.models.info.BankAccountInfoWithoutBalance;
import com.techsage.banking.repositories.BankAccountRepository;
import com.techsage.banking.services.interfaces.BankAccountService;
import org.iban4j.Iban;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.*;
import java.util.List;

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
    public List<BankAccountDto> getAll() {
        List<BankAccount> bankAccounts = (List<BankAccount>)bankAccountRepository.findAll();
        return bankAccounts.stream().map(bankAccount -> modelMapper.map(bankAccount, BankAccountDto.class)).toList();
    }

    @Override
    public List<BankAccountDto> findByUserAndType(User user, BankAccountType type) {
        List<BankAccount> bankAccounts;
        if (type == null) {
            bankAccounts = bankAccountRepository.findByUser(user);
        } else {
            bankAccounts = bankAccountRepository.findByUserAndType(user, type);
        }
        return bankAccounts.stream().map(bankAccount -> modelMapper.map(bankAccount, BankAccountDto.class)).toList();
    }

    @Override
    public List<BankAccountDto> findByType(BankAccountType type) {
        List<BankAccount> bankAccounts = bankAccountRepository.findByType(type);
        return bankAccounts.stream().map(bankAccount -> modelMapper.map(bankAccount, BankAccountDto.class)).toList();
    }

    @Override
    public BankAccount getByIban(Iban iban) {
        return bankAccountRepository.findByIban(iban);
    }

    @Override
    public List<BankAccountInfoWithoutBalance> findByFirstNameAndLastName(String firstName, String lastName) {
        List<BankAccount> bankAccounts = bankAccountRepository
                .findByUserFirstNameStartingWithIgnoreCaseAndUserLastNameStartingWithIgnoreCaseAndType(firstName, lastName, BankAccountType.CHECKING);
        return bankAccounts.stream()
                .map(bankAccount -> modelMapper.map(bankAccount, BankAccountInfoWithoutBalance.class))
                .toList();
    }


    @Override
    public BankAccount create(User user, BankAccountType bankAccountType, BigDecimal absoluteMinimumBalance, BigDecimal balance) {
        BankAccount bankAccount = new BankAccount();
        boolean exists = false;
        do {
            Iban iban = IbanHelper.generateIban();
            if (this.getByIban(iban) == null) {
                bankAccount.setIban(iban);
                exists = true;
            }
        } while (!exists);
        bankAccount.setUser(user);
        bankAccount.setType(bankAccountType);
        bankAccount.setAbsoluteMinimumBalance(absoluteMinimumBalance);
        bankAccount.setBalance(balance);
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public BankAccount update(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public BankAccount getById(long id) {
        return bankAccountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Bank account not found"));
    }
}
