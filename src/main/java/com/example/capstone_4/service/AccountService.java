package com.example.capstone_4.service;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.repository.AccountRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    //method to generate the next formatted Account ID string (e.g., 0001, 0002)
    private String generateNextAccountId() {
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

        return accountRepository.save(account);
    }


    //login
    public Account login(String username, String password) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Validation: Prevent login for disabled users
        if ("DISABLED".equalsIgnoreCase(account.getRole())) {
            throw new RuntimeException("This account has been disabled. Please contact support.");
        }

        if (!account.getPassword().equals(password)) {
            throw new RuntimeException("Invalid username or password");
        }
        return account;

    }


}
