package com.example.capstone_4;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.repository.AccountRepository;
import com.example.capstone_4.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setUsername("testuser");
        account.setPassword("password");
    }

    // ================= REGISTER SUCCESS =================
    @Test
    void register_ShouldSaveAccount_WhenUsernameIsUnique() {

        when(accountRepository.findByUsername("testuser"))
                .thenReturn(Optional.empty());

        when(accountRepository.findTopByOrderByIdDesc())
                .thenReturn(null);

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account saved = accountService.register(account);

        assertNotNull(saved);
        assertEquals("USER", saved.getRole());
        assertEquals("0001", saved.getAccountId());
        assertEquals("encodedPassword", saved.getPassword());

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    // ================= REGISTER DUPLICATE USERNAME =================
    @Test
    void register_ShouldThrowException_WhenUsernameExists() {

        when(accountRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> accountService.register(account)
        );

        assertTrue(exception.getMessage().contains("already taken"));
    }

    // ================= LOGIN SUCCESS =================
    @Test
    void login_ShouldReturnAccount_WhenCredentialsAreCorrect() {

        when(accountRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches("password", "password"))
                .thenReturn(true);

        Account loggedIn = accountService.login("testuser", "password");

        assertNotNull(loggedIn);
        assertEquals("testuser", loggedIn.getUsername());
    }

    // ================= LOGIN USER NOT FOUND =================
    @Test
    void login_ShouldThrowException_WhenUserNotFound() {

        when(accountRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> accountService.login("unknown", "password")
        );

        assertEquals("User not found", exception.getMessage());
    }

    // ================= LOGIN INVALID PASSWORD =================
    @Test
    void login_ShouldThrowException_WhenPasswordIsIncorrect() {

        when(accountRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches("wrong", "password"))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> accountService.login("testuser", "wrong")
        );

        assertEquals("Invalid username or password", exception.getMessage());
    }
}
