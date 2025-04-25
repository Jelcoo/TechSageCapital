package com.techsage.banking.controllers;

public class BankAccountController {
    @Autowired
    private final BankAccountService bankAccountService;

    @GetMapping("/bankAccount/{id}")
    public BankAccountDto getBankAccount(@PathVariable long id) {
        return bankAccountService.getById(id);
    }

    @GetMapping("/bankAccounts")
    public List<BankAccountDto> getBankAccounts() {
        return bankAccountService.getAll();
    }

    @PostMapping("/bankAccount/create")
    public void createBankAccount(@RequestBody BankAccountDto bankAccount) {
        bankAccountService.create(bankAccount);
    }

    @PutMapping("/bankAccount/update")
    public void updateBankAccount(@RequestBody BankAccountDto bankAccount) {
        bankAccountService.update(bankAccount);
    }
}
