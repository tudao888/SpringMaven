package com.SpringMaven.controller;

import com.SpringMaven.model.Account;
import com.SpringMaven.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AccountService accountService;


    @PutMapping("/block/{id}")
    public ResponseEntity<?> blockAccount(@PathVariable int id) {
        Account account = accountService.findAccountById(id);
        if (account.getStatus() == 1) {
            account.setStatus(2);
        } else {
            account.setStatus(1);
        }
        accountService.save(account);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PutMapping("/change-role-user/{id}")
    public ResponseEntity<Account> changeRoleUser(@PathVariable int id) {
        Account account = accountService.findAccountById(id);
        if (account.getRoles().get(0).getName().equals("ROLE_USER")) {
            account.getRoles().get(0).setName("ROLE_PROVIDER");
            return new ResponseEntity<>(accountService.save(account), HttpStatus.OK);
        } else {
            account.getRoles().get(0).setName("ROLE_PROVIDER");
            return new ResponseEntity<>(accountService.save(account), HttpStatus.OK);
        }
    }

}
