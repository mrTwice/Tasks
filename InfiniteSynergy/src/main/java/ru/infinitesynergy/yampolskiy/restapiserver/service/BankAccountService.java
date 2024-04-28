package ru.infinitesynergy.yampolskiy.restapiserver.service;

import ru.infinitesynergy.yampolskiy.restapiserver.entities.BankAccount;
import ru.infinitesynergy.yampolskiy.restapiserver.repository.BankAccountRepository;

import java.util.List;

public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public BankAccount createNewBankAccount (Long userId) {
        BankAccount newBankAccount = bankAccountRepository.createBankAccount(BankAccount.createBankAccount(userId));
        return newBankAccount;
    }

    public BankAccount getBankAccountByUserId(Long userId) {
        return bankAccountRepository.readBankAccount(userId);
    }

    public void updateBankAccount(BankAccount bankAccount) {
        bankAccountRepository.updateBankAccount(bankAccount);
    }

    public void deleteBankAccount(Long id) {
        bankAccountRepository.deleteBankAccount(id);
    }

    public List<Long> getUserBankAccounts(Long userId) {
        return bankAccountRepository.getBankAccountIdsByUserId(userId);
    }
}
