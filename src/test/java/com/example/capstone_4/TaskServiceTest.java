package com.example.capstone_4;

import com.example.capstone_4.Exceptions.*;
import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import com.example.capstone_4.repository.AccountRepository;
import com.example.capstone_4.repository.TaskRepository;
import com.example.capstone_4.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TaskService taskService;

    private Account account;
    private Task task;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setAccountId("A1");

        task = new Task();
        task.setId(1L);
        task.setTaskName("Test Task");
        task.setTaskDescription("Test Description");
    }

    // =========================
    // VALIDATION TESTS
    // =========================

    @Test
    void createTask_shouldThrow_whenTaskNameIsNull() {
        task.setTaskName(null);

        when(accountRepository.findByAccountId("A1"))
                .thenReturn(Optional.of(account));

        assertThrows(MissingRequiredFieldException.class,
                () -> taskService.createTask("A1", task));
    }

    @Test
    void createTask_shouldThrow_whenTaskNameTooLong() {
        task.setTaskName("A".repeat(61));

        when(accountRepository.findByAccountId("A1"))
                .thenReturn(Optional.of(account));

        assertThrows(ExcessiveLengthException.class,
                () -> taskService.createTask("A1", task));
    }

    @Test
    void createTask_shouldThrow_whenAccountNotFound() {
        when(accountRepository.findByAccountId("A1"))
                .thenReturn(Optional.empty());

        assertThrows(AccountDoesNotExistException.class,
                () -> taskService.createTask("A1", task));
    }

    // =========================
    // CREATE TEST
    // =========================

    @Test
    void createTask_shouldSetDateAndSave() {
        when(accountRepository.findByAccountId("A1"))
                .thenReturn(Optional.of(account));
        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        Task saved = taskService.createTask("A1", task);

        assertNotNull(saved);
        assertNotNull(task.getTaskDate());
        assertEquals(account, task.getTaskId());
        verify(taskRepository).save(task);
    }

    // =========================
    // GET TESTS
    // =========================

    @Test
    void getAllTasks_shouldReturnList() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(1, tasks.size());
    }

    @Test
    void getTaskById_shouldReturnOptional() {
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        Optional<Task> result = taskService.getTaskById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void getByTaskByUserId_shouldReturnTasks() {
        when(accountRepository.findByAccountId("A1"))
                .thenReturn(Optional.of(account));
        when(taskRepository.findByTaskId(account))
                .thenReturn(List.of(task));

        List<Task> tasks = taskService.getByTaskByUserId("A1");

        assertEquals(1, tasks.size());
    }

    // =========================
    // UPDATE TEST
    // =========================

    @Test
    void updateTaskById_shouldUpdateFields() {
        Task updated = new Task();
        updated.setTaskName("Updated");
        updated.setTaskDescription("Updated Desc");

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        Optional<Task> result = taskService.updateTaskById(1L, updated);

        assertTrue(result.isPresent());
        assertEquals("Updated", task.getTaskName());
        assertEquals("Updated Desc", task.getTaskDescription());
    }

    // =========================
    // DELETE TESTS
    // =========================

    @Test
    void deleteTaskById_shouldReturnTrue() {
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        boolean result = taskService.deleteTaskById(1L);

        assertTrue(result);
        verify(taskRepository).delete(task);
    }


    @Test
    void deleteTask_shouldThrow_whenTaskNotFound() {
        when(accountRepository.findByAccountId("A1"))
                .thenReturn(Optional.of(account));
        when(taskRepository.getTaskByTaskId(account))
                .thenReturn(Optional.empty());

        assertThrows(TaskIdDoesNotExistException.class,
                () -> taskService.deleteTask("A1"));
    }
}
