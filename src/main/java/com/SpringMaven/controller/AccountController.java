package com.SpringMaven.controller;

import com.SpringMaven.model.*;
import com.SpringMaven.service.AccountService;
import com.SpringMaven.service.JwtService;
import com.SpringMaven.service.ProductService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
public class AccountController {
   public final static Logger logger = Logger.getLogger(AccountController.class);
    @Autowired
    AccountService accountService;

    @Autowired
    ProductService productService;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        try {
            if (account.getUsername() == null || account.getUsername().isEmpty() || account.getPassword() == null || account.getPassword().isEmpty()) {
                Object error = "Tên đăng nhập và mật khẩu không được để trống";
                logger.error(error);
                return ResponseEntity.badRequest().body(error);
            } else {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(account.getUsername(), account.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                String token = jwtService.createToken(authentication);
                Account account1 = accountService.findAccountByUsername(account.getUsername());
                if (account1.getStatus() == 2) {
                    Object error = "Tài khoản của bạn đã bị khóa";
                    logger.error(error);
                    return ResponseEntity.badRequest().body(error);
                } else {
                    AccountToken accountToken = new AccountToken(token);
                    logger.info("Lấy token thành công");
                    return ResponseEntity.ok(accountToken);
                }
            }
        } catch (AuthenticationException e) {
            Object error = "Tên đăng nhập hoặc mật khẩu không chính xác";
            logger.error(error);
            return ResponseEntity.badRequest().body(error);
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
