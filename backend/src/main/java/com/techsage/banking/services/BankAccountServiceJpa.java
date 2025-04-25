package com.techsage.banking.services;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.BankAccountDto;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.repositories.BankAccountRepository;
import com.techsage.banking.services.interfaces.BankAccountService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountServiceJpa implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    ModelMapper modelMapper;

    public BankAccountServiceJpa(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public List<BankAccountDto> getAll() {
        List<BankAccount> bankAccounts = (List<BankAccount>)bankAccountRepository.findAll();
        return bankAccounts.stream().map(bankAccount -> modelMapper.map(bankAccount, BankAccountDto.class)).toList();
    }

    @Override
    public BankAccount getById(long id) {return bankAccountRepository.findById(id).get();}

    @Override
    public BankAccount create(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public BankAccount update(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public void delete(long id) {
        bankAccountRepository.deleteById(id);
    }
}
