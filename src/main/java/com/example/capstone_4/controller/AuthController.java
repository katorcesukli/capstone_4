package com.example.capstone_4.controller;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.service.AccountService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.capstone_4.security.JwtUtil;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AccountService accountService;
    private final JwtUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        try {
            Account created = accountService.register(account);

            return ResponseEntity.ok(Map.of(
                    "username", created.getUsername(),
                    "role", created.getRole()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            Account account = accountService.login(username, password);
            session.setAttribute("loggedUser", account);

            //JWT Token generate
            String token = jwtUtil.generateToken(account.getUsername(), account.getRole());


            // Return role info to frontend for redirect
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "username", account.getUsername(),
                    "role", account.getRole() // "ADMIN" or "USER"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }



}
