package com.example.capstone_4;

import com.example.capstone_4.model.Task;
import com.example.capstone_4.repository.TaskRepository;
import com.example.capstone_4.service.AdminTasksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
}
