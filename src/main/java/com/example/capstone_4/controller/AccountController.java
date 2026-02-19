package com.example.capstone_4.controller;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.service.AdminTasksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")  // base path for user/account endpoints
@RequiredArgsConstructor
public class AccountController {

    private final AdminTasksService adminService;

    // ---------------- USER CRUD ----------------

    // Get all users
    @GetMapping
    public List<Account> getAllUsers() {
        return adminService.getAllUsers();
    }

    // Get a user by accountId
    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getUserById(@PathVariable String accountId) {
        return adminService.getUserByAccountId(accountId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new user
    @PostMapping
    public ResponseEntity<Account> createUser(@RequestBody Account account) {
        return ResponseEntity.ok(adminService.createUser(account));
    }

    // Update existing user
    @PutMapping("/edit/{accountId}")
    public ResponseEntity<Account> updateUser(@PathVariable String accountId, @RequestBody Account account) {
        return adminService.updateUser(accountId, account)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete user
    @DeleteMapping("/{accountId}")
    public ResponseEntity<String> deleteUser(@PathVariable String accountId) {
        try {
            boolean deleted = adminService.deleteUser(accountId);
            return deleted ? ResponseEntity.ok("User deleted successfully") : ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
