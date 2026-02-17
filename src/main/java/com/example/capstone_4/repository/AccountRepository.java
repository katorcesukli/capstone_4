package com.example.capstone_4.repository;

import com.example.capstone_4.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findTopByOrderByIdDesc();

    Optional<Account> findByUsername(String username);

    Optional<Account> findByAccountId(String accountId);
}
