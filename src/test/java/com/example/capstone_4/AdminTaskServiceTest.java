package com.example.capstone_4;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import com.example.capstone_4.repository.AccountRepository;
import com.example.capstone_4.repository.TaskRepository;
import com.example.capstone_4.service.AdminTasksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AdminTasksServiceTest {

    @Autowired
    private AdminTasksService adminTasksService;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task();
        task1.setId(1L);
        task1.setTaskName("Task 1");
        task1.setTaskDescription("Description 1");
        task1.setTaskStatus("PENDING");
        task1.setTaskDate(LocalDate.now());

        task2 = new Task();
        task2.setId(2L);
        task2.setTaskName("Task 2");
        task2.setTaskDescription("Description 2");
        task2.setTaskStatus("DONE");
        task2.setTaskDate(LocalDate.now());
    }

    // =========================
    // GET ALL TASKS
    // =========================
    @Test
    void testGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = adminTasksService.getAllTasks();

        assertEquals(2, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    // =========================
    // GET TASK BY ID
    // =========================
    @Test
    void testGetTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        Optional<Task> found = adminTasksService.getTaskById(1L);

        assertTrue(found.isPresent());
        assertEquals("Task 1", found.get().getTaskName());
        verify(taskRepository).findById(1L);
    }

    // =========================
    // CREATE TASK
    // =========================
    @Test
    void testCreateNewTask() {
        when(taskRepository.save(task1)).thenReturn(task1);

        Task saved = adminTasksService.createNewTask(task1);

        assertNotNull(saved);
        assertEquals("Task 1", saved.getTaskName());
        verify(taskRepository).save(task1);
    }

    // =========================
    // UPDATE TASK
    // =========================
    @Test
    void testUpdateTaskById() {
        Task updatedTask = new Task();
        updatedTask.setTaskName("Updated Task");
        updatedTask.setTaskDescription("Updated Desc");
        updatedTask.setTaskStatus("DONE");
        updatedTask.setTaskDate(LocalDate.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        Optional<Task> result = adminTasksService.updateTaskById(1L, updatedTask);

        assertTrue(result.isPresent());
        assertEquals("Updated Task", result.get().getTaskName());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(task1);
    }

    // =========================
    // DELETE TASK
    // =========================
    @Test
    void testDeleteTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        boolean deleted = adminTasksService.deleteTaskById(1L);

        assertTrue(deleted);
        verify(taskRepository).delete(task1);
    }

    @Test
    void testDeleteTaskById_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        boolean deleted = adminTasksService.deleteTaskById(99L);

        assertFalse(deleted);
        verify(taskRepository, never()).delete(any());
    }

    //USERS TEST
    private Account user1;
    private Account user2;

    @BeforeEach
    void setUpUsers() {
        user1 = new Account();
        user1.setAccountId("U001");
        user1.setUsername("Alice");
        user1.setPassword("pass1");
        user1.setRole("USER");

        user2 = new Account();
        user2.setAccountId("U002");
        user2.setUsername("Bob");
        user2.setPassword("pass2");
        user2.setRole("ADMIN");
    }

    // =========================
    // GET ALL USERS
    // =========================
    @Test
    void testGetAllUsers() {
        when(accountRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<Account> users = adminTasksService.getAllUsers();

        assertEquals(2, users.size());
        // Passwords should be null
        assertNull(users.get(0).getPassword());
        assertNull(users.get(1).getPassword());
        verify(accountRepository).findAll();
    }

    // =========================
    // GET USER BY ACCOUNT ID
    // =========================
    @Test
    void testGetUserByAccountId() {
        when(accountRepository.findByAccountId("U001")).thenReturn(Optional.of(user1));

        Optional<Account> user = adminTasksService.getUserByAccountId("U001");

        assertTrue(user.isPresent());
        assertEquals("Alice", user.get().getUsername());
        assertNull(user.get().getPassword());
        verify(accountRepository).findByAccountId("U001");
    }

    // =========================
    // CREATE USER
    // =========================
    @Test
    void testCreateUser() {
        when(passwordEncoder.encode("pass1")).thenReturn("encodedPass1");
        when(accountRepository.save(any(Account.class))).thenReturn(user1);

        Account created = adminTasksService.createUser(user1);

        assertEquals("USER", created.getRole());
        assertEquals("encodedPass1", created.getPassword());
        verify(passwordEncoder).encode("pass1");
        verify(accountRepository).save(user1);
    }

    // =========================
    // UPDATE USER
    // =========================
    @Test
    void testUpdateUser() {
        Account updatedData = new Account();
        updatedData.setUsername("AliceUpdated");
        updatedData.setPassword("newpass");
        updatedData.setRole("ADMIN");

        when(accountRepository.findByAccountId("U001")).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNewPass");
        when(accountRepository.save(any(Account.class))).thenReturn(user1);

        Optional<Account> updated = adminTasksService.updateUser("U001", updatedData);

        assertTrue(updated.isPresent());
        assertEquals("AliceUpdated", updated.get().getUsername());
        assertEquals("ADMIN", updated.get().getRole());
        assertNull(updated.get().getPassword()); // password is never returned
        verify(accountRepository).findByAccountId("U001");
        verify(accountRepository).save(user1);
    }

    // =========================
    // DELETE USER
    // =========================
    @Test
    void testDeleteUser() {
        when(accountRepository.findByAccountId("U001")).thenReturn(Optional.of(user1));
        when(accountRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        boolean deleted = adminTasksService.deleteUser("U001");

        assertTrue(deleted);
        verify(accountRepository).delete(user1);
    }

}
