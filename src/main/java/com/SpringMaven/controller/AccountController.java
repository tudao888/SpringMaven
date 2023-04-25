package com.SpringMaven.controller;

import com.SpringMaven.model.*;
import com.SpringMaven.service.AccountService;
import com.SpringMaven.service.JwtService;
import com.SpringMaven.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
public class AccountController {
    @Autowired
    AccountService accountService;

    @Autowired
    ProductService productService;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    public boolean checkAccount(Account account) {
        Account accountChecked = accountService.findAccountByUsername(account.getUsername());
        if (accountChecked != null && account.getPassword().equals(accountChecked.getPassword())) {
            return true;
        } else {
            return false;
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(account.getUsername(), account.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtService.createToken(authentication);
        Account account1 = accountService.findAccountByUsername(account.getUsername());

        if (checkAccount(account)) {
            if (account1.getStatus() == 2) {
                ErrorResponse errorResponse = new ErrorResponse("Tài khoản đã bị khóa");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            } else {
                AccountToken accountToken = new AccountToken(token);
                return ResponseEntity.ok(accountToken);
            }
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Tên đăng nhập hoặc mật khẩu không đúng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }





    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setId(1);
        roles.add(role);
        account.setRoles(roles);
        account.setStatus(1);
        accountService.createAccount(account);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/user/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }


}
