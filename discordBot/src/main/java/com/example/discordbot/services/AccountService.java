package com.example.discordbot.services;

import com.example.discordbot.commands.account.entities.Account;
import com.example.discordbot.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {


    private final AccountRepository accountRepository;

    public void saveAccount(Account account) {
        this.accountRepository.save(account);
    }

    public Optional<Account> findAccountById(Long id) {
        return this.accountRepository.findById(id);
    }

    public void updateAccount(Account account) {
        this.accountRepository.save(account);
    }

    public List<Account> findAllAccounts() {
        return this.accountRepository.findAll();
    }

    public void removeAccountById(Long id) {
        this.accountRepository.deleteById(id);
    }

}
