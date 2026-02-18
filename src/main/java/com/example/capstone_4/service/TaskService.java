package com.example.capstone_4.service;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import com.example.capstone_4.repository.AccountRepository;
import com.example.capstone_4.repository.TaskRepository;
import com.example.capstone_4.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final AccountRepository accountRepository;

    //method to generate the next formatted Task ID string (e.g., 0001, 0002)
    private String generateNextAccountId() {
        Task lastTask = taskRepository.findTopByOrderByIdDesc();
        long nextId = (lastTask != null) ? lastTask.getId() + 1 : 1;
        return String.format("%04d", nextId); // e.g., "0001", "0002"
    }

    //ADMIN TASKS CRUD STUFF
    // Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Get task by ID
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Create new task
    public Task createNewTask(Task task, String accountId) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        task.setTaskId(account);
        return taskRepository.save(task);
    }

    // Update task by ID
    public Optional<Task> updateTaskById(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            task.setTaskName(updatedTask.getTaskName());
            task.setTaskDescription(updatedTask.getTaskDescription());
            task.setTaskStatus(updatedTask.getTaskStatus());
            task.setTaskDate(updatedTask.getTaskDate());
            return taskRepository.save(task);
        });
    }

    // Delete task by ID
    public boolean deleteTaskById(Long id) {
        return taskRepository.findById(id).map(task -> {
            taskRepository.delete(task);
            return true;
        }).orElse(false);
    }
    //END OF ADMIN CRUD STUFF
}
