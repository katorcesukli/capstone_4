package com.example.capstone_4;

import com.example.capstone_4.Exceptions.*;
import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import com.example.capstone_4.repository.AccountRepository;
import com.example.capstone_4.repository.TaskRepository;
import com.example.capstone_4.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        MockitoAnnotations.openMocks(this);

        account = new Account();
        account.setAccountId("user123");
        account.setUsername("TestUser");

        task = new Task();
        task.setTaskName("Sample Task");
        task.setTaskDescription("Sample Description");
        task.setTaskStatus("PENDING");
        task.setTaskId(account);
        task.setTaskDate(LocalDate.now());
    }

    /*** ADMIN CRUD TESTS ***/
    @Test
    void testGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));
        List<Task> tasks = taskService.getAllTasks();
        assertEquals(1, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testGetTaskById_Found() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        Optional<Task> result = taskService.getTaskById(1L);
        assertTrue(result.isPresent());
        assertEquals("Sample Task", result.get().getTaskName());
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Task> result = taskService.getTaskById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void testCreateNewTask_Success() {
        when(accountRepository.findByAccountId("user123")).thenReturn(Optional.of(account));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task newTask = new Task();
        newTask.setTaskName("Sample Task");
        newTask.setTaskDescription("Sample Description");

        Task savedTask = taskService.createNewTask(newTask, "user123");
        assertEquals("Sample Task", savedTask.getTaskName());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testCreateNewTask_AccountNotFound() {
        when(accountRepository.findByAccountId("user123")).thenReturn(Optional.empty());
        Task newTask = new Task();
        newTask.setTaskName("Sample Task");
        newTask.setTaskDescription("Sample Description");

        assertThrows(AccountDoesNotExistException.class, () -> taskService.createNewTask(newTask, "user123"));
    }

    @Test
    void testUpdateTaskById_Success() {
        Task updatedTask = new Task();
        updatedTask.setTaskName("Updated Task");
        updatedTask.setTaskDescription("Updated Description");
        updatedTask.setTaskStatus("DONE");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Optional<Task> result = taskService.updateTaskById(1L, updatedTask);

        assertTrue(result.isPresent());
        assertEquals("Updated Task", result.get().getTaskName());
        assertEquals("DONE", result.get().getTaskStatus());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testDeleteTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        boolean result = taskService.deleteTaskById(1L);

        assertTrue(result);
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void testDeleteTaskById_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = taskService.deleteTaskById(1L);

        assertFalse(result);
        verify(taskRepository, never()).delete(any());
    }

    /*** USER-SPECIFIC OPERATIONS ***/
    @Test
    void testGetByTaskByUserId_Success() {
        when(accountRepository.findByAccountId("user123")).thenReturn(Optional.of(account));
        when(taskRepository.findByTaskId(account)).thenReturn(Arrays.asList(task));

        List<Task> tasks = taskService.getByTaskByUserId("user123");
        assertEquals(1, tasks.size());
        assertEquals("Sample Task", tasks.get(0).getTaskName());
    }

    @Test
    void testCreateTask_Success() {
        when(accountRepository.findByAccountId("user123")).thenReturn(Optional.of(account));

        // Return the argument passed to save(), so the name matches
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task newTask = new Task();
        newTask.setTaskName("New Task");
        newTask.setTaskDescription("New Description");

        Task savedTask = taskService.createTask("user123", newTask);

        assertEquals("New Task", savedTask.getTaskName());
        assertEquals("New Description", savedTask.getTaskDescription());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

//    @Test
//    void testDeleteTask_Success() {
//        when(accountRepository.findByAccountId("user123")).thenReturn(Optional.of(account));
//        when(taskRepository.getTaskByTaskId(account)).thenReturn(Optional.of(List.of(task)));
//
//        // Service should pick the first task from the list
//        Task deletedTask = taskService.deleteTask("user123");
//
//        assertEquals("Sample Task", deletedTask.getTaskName());
//        verify(taskRepository, times(1)).delete(task);
//    }



    @Test
    void testDeleteTask_TaskNotFound() {
        when(accountRepository.findByAccountId("user123")).thenReturn(Optional.of(account));
        when(taskRepository.getTaskByTaskId(account)).thenReturn(Optional.empty());

        assertThrows(TaskIdDoesNotExistException.class, () -> taskService.deleteTask("user123"));
    }
}
