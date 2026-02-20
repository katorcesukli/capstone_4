package com.example.capstone_4.service;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import com.example.capstone_4.repository.AccountRepository;
import com.example.capstone_4.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminTasksService {

    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

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
    public Task createNewTask(Task task) {
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

    //ADMIN USERS CRUD STUFF
    public List<Account> getAllUsers() {
        List<Account> accounts = accountRepository.findAll();
        accounts.forEach(a -> a.setPassword(null)); // hide passwords
        return accounts;
    }

    public Optional<Account> getUserByAccountId(String accountId) {
        return accountRepository.findByAccountId(accountId)
                .map(a -> { a.setPassword(null); return a; });
    }

    public Account createUser(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole(account.getRole().toUpperCase());
        account.setAccountId(accountService.generateNextAccountId());
        return accountRepository.save(account);
    }

    public Optional<Account> updateUser(String accountId, Account updatedAccount) {
        return accountRepository.findByAccountId(accountId)
                .map(existing -> {
                    if (updatedAccount.getUsername() != null) existing.setUsername(updatedAccount.getUsername());
                    if (updatedAccount.getPassword() != null && !updatedAccount.getPassword().isEmpty())
                        existing.setPassword(passwordEncoder.encode(updatedAccount.getPassword()));
                    if (updatedAccount.getRole() != null) existing.setRole(updatedAccount.getRole().toUpperCase());
                    Account saved = accountRepository.save(existing);
                    saved.setPassword(null);
                    return saved;
                });
    }

    public boolean deleteUser(String accountId) {
        return accountRepository.findByAccountId(accountId).map(user -> {
            // prevent deleting last admin
            if ("ADMIN".equals(user.getRole()) &&
                    accountRepository.findAll().stream().filter(a -> "ADMIN".equals(a.getRole())).count() == 1) {
                throw new RuntimeException("Cannot delete last admin");
            }
            accountRepository.delete(user);
            return true;
        }).orElse(false);
    }
}