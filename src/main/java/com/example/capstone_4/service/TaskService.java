package com.example.capstone_4.service;

import com.example.capstone_4.Exceptions.*;
import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import com.example.capstone_4.repository.AccountRepository;
import com.example.capstone_4.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;

    /*** Validation ***/
    private void validateEntries(String taskName, String taskDescription){
        if (taskName == null || taskName.isEmpty())
            throw new MissingRequiredFieldException("Task Name cannot be blank");
        if (taskName.length() > 60)
            throw new ExcessiveLengthException("Task name is too long. Max 60 characters allowed");
        if (taskDescription!=null && taskDescription.length() > 200)
            throw new ExcessiveLengthException("Task description is too long. Max 200 characters allowed");
    }

    /*** ADMIN CRUD ***/
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task createNewTask(Task task, String accountId) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountDoesNotExistException("Account not found: " + accountId));
        validateEntries(task.getTaskName(), task.getTaskDescription());
        task.setTaskId(account);
        if (task.getTaskDate() == null) {
            task.setTaskDate(LocalDate.now());
        }
        return taskRepository.save(task);
    }

    public Optional<Task> updateTaskById(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            validateEntries(updatedTask.getTaskName(), updatedTask.getTaskDescription());
            task.setTaskName(updatedTask.getTaskName());
            task.setTaskDescription(updatedTask.getTaskDescription());
            task.setTaskStatus(updatedTask.getTaskStatus());
            task.setTaskDate(updatedTask.getTaskDate() != null ? updatedTask.getTaskDate() : LocalDate.now());
            return taskRepository.save(task);
        });
    }

    public boolean deleteTaskById(Long id) {
        return taskRepository.findById(id).map(task -> {
            taskRepository.delete(task);
            return true;
        }).orElse(false);
    }

    /*** USER-SPECIFIC OPERATIONS ***/
    public List<Task> getByTaskByUserId(String accountId){
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountDoesNotExistException("Account not found: " + accountId));

        return taskRepository.findByTaskIdOrderByTaskDate(account);
    }


    public Task createTask(String accountId, Task task){
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountDoesNotExistException("Account not found: " + accountId));
        validateEntries(task.getTaskName(), task.getTaskDescription());
        task.setTaskId(account);
        if (task.getTaskDate() == null) {
            task.setTaskDate(LocalDate.now());
        }
        return taskRepository.save(task);
    }

    public Task deleteTask(String accountId){
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountDoesNotExistException("Account not found: " + accountId));
        Task task = (Task) taskRepository.getTaskByTaskId(account)
                .orElseThrow(() -> new TaskIdDoesNotExistException("Task not found for account: " + accountId));
        taskRepository.delete(task);
        return task;
    }

}
