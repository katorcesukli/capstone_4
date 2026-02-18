package com.example.capstone_4.repository;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // Find by account_id
    Optional<Account> findByAccountId(String accountId);

    // Find by username
    Optional<Account> findByUsername(String username);
}
