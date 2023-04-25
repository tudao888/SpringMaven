package com.SpringMaven.service;

import com.SpringMaven.model.Account;
import com.SpringMaven.repository.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepo accountRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepo.findAccountByUsername(username);
        return new User(account.getUsername(), account.getPassword(), account.getRoles());
    }

    public Account findAccountByUsername(String username) {
        return accountRepo.findAccountByUsername(username);
    }

    public Account findAccountById(int id) {
        return accountRepo.findAccountById(id);
    }

    public Account save(Account account) {
        accountRepo.save(account);
        return account;
    }

    public Account createAccount(Account account) {
        accountRepo.save(account);
        return account;
    }
}
