package com.example.capstone_4.service;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import com.example.capstone_4.repository.AccountRepository;
import com.example.capstone_4.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminTasksServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminTasksService adminTasksService;

    private Task task;
    private Account account;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTaskName("Test Task");

        account = new Account();
        account.setAccountId("A1");
        account.setUsername("admin");
        account.setPassword("password");
        account.setRole("ADMIN");
    }

    // ======================
    // TASK TESTS
    // ======================

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));

        List<Task> tasks = adminTasksService.getAllTasks();

        assertEquals(1, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_shouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<Task> result = adminTasksService.getTaskById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Task", result.get().getTaskName());
    }

    @Test
    void createNewTask_shouldSaveTask() {
        when(taskRepository.save(task)).thenReturn(task);

        Task saved = adminTasksService.createNewTask(task);

        assertNotNull(saved);
        verify(taskRepository).save(task);
    }

    @Test
    void deleteTaskById_shouldReturnTrue() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        boolean result = adminTasksService.deleteTaskById(1L);

        assertTrue(result);
        verify(taskRepository).delete(task);
    }

    // ======================
    // USER TESTS
    // ======================

    @Test
    void createUser_shouldEncodePasswordAndUppercaseRole() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account saved = adminTasksService.createUser(account);

        assertEquals("encodedPassword", account.getPassword());
        assertEquals("ADMIN", account.getRole());
        verify(accountRepository).save(account);
    }

    @Test
    void getUserByAccountId_shouldHidePassword() {
        when(accountRepository.findByAccountId("A1"))
                .thenReturn(Optional.of(account));

        Optional<Account> result = adminTasksService.getUserByAccountId("A1");

        assertTrue(result.isPresent());
        assertNull(result.get().getPassword());
    }

    @Test
    void deleteUser_shouldThrowException_whenDeletingLastAdmin() {
        when(accountRepository.findByAccountId("A1"))
                .thenReturn(Optional.of(account));
        when(accountRepository.findAll())
                .thenReturn(List.of(account));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminTasksService.deleteUser("A1"));

        assertEquals("Cannot delete last admin", exception.getMessage());
    }
}
