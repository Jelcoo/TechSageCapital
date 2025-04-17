package com.techsage.banking.services;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.repositories.BankAccountRepository;
import com.techsage.banking.services.interfaces.BankAccountService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountServiceJpa implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    public BankAccountServiceJpa(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public List<BankAccount> getAll() {
        return (List<BankAccount>) bankAccountRepository.findAll();
    }

    @Override
    public BankAccount getById(int id) {
        return bankAccountRepository.findById(id).get();
    }

    @Override
    public BankAccount create(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public BankAccount update(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public void delete(int id) {
        bankAccountRepository.deleteById(id);
    }
}
