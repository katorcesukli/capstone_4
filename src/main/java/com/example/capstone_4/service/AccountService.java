package com.example.capstone_4.service;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.repository.AccountRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    //method to generate the next formatted Account ID string (e.g., 0001, 0002)
    protected String generateNextAccountId() {
        Account lastAccount = accountRepository.findTopByOrderByIdDesc();
        long nextId = (lastAccount != null) ? lastAccount.getId() + 1 : 1;
        return String.format("%04d", nextId);
    }

    //register
    public Account register(Account account) {
        //unique username validation
        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
            throw new RuntimeException("Username '" + account.getUsername() + "' is already taken");
        }

        account.setRole("USER");
        // FIX: Replaced UUID logic with the formatted sequence logic
        account.setAccountId(generateNextAccountId());

        //password hash
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        return accountRepository.save(account);
    }


    //login
    public Account login(String username, String rawPassword) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify the password
        if (!passwordEncoder.matches(rawPassword, account.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return account;
    }

    }


