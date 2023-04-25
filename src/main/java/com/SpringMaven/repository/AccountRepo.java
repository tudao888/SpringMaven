package com.SpringMaven.repository;

import com.SpringMaven.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends JpaRepository<Account, Integer> {
    Account findAccountByUsername(String username);

    Account findAccountById(int id);

}
