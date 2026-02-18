package com.example.capstone_4.service;

import com.example.capstone_4.Exceptions.*;
import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import com.example.capstone_4.repository.AccountRepository;
import com.example.capstone_4.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    public TaskService(TaskRepository taskRepository, AccountRepository accountRepository){
        this.taskRepository=taskRepository;
        this.accountRepository =accountRepository;
    }

    private boolean validateEntries(String taskName, String taskDescription){
        if (taskName == null ||taskName.isEmpty())
            throw new MissingRequiredFieldException("Task Name cannot be blank");
        if(taskName.length()>60 )
            throw new ExcessiveLengthException("Task name is too long. Try to keep it below 60 characters!");
        if(taskDescription.length()>200)
            throw new ExcessiveLengthException("Task description is too long. Try to keep it below 200 characters!");
        return true;
    }
    //Local date time is automatically localdate.now
    public Task createTask(String accountId,
                           String taskName, String taskStatus,String taskDescription){
        Optional<Account> user = accountRepository.findByAccountId(accountId);
        validateEntries(taskName,taskDescription);
        if (user.isEmpty())
            throw new AccountDoesNotExistException("Account with that Account ID does not exist");
        Optional<Task> task = taskRepository.getTaskByTaskId(user.get());

        if (task.isEmpty()){
            Task newTask=new Task();
            newTask.setTaskDate(LocalDate.now());
            newTask.setTaskDescription(taskDescription);
            newTask.setTaskId(user.get());
            newTask.setTaskName(taskName);
            newTask.setTaskStatus(taskStatus);

            taskRepository.save(newTask);
            return newTask;
        }
        else throw new TaskIdExistsException("Task for that account ID already exists");

    }
    //User entered a date
    public Task createTask(LocalDate taskDate, String accountId,
                           String taskName, String taskStatus,String taskDescription){
        Optional<Account> user = accountRepository.findByAccountId(accountId);
        validateEntries(taskName,taskDescription);
        if (user.isEmpty())
            throw new AccountDoesNotExistException("Account with that Account ID does not exist");
        Optional<Task> task = taskRepository.getTaskByTaskId(user.get());

        if (task.isEmpty()){
            Task newTask=new Task();
            newTask.setTaskDate(taskDate);
            newTask.setTaskDescription(taskDescription);
            newTask.setTaskId(user.get());
            newTask.setTaskName(taskName);
            newTask.setTaskStatus(taskStatus);

            taskRepository.save(newTask);
            return newTask;
        }
        else throw new TaskIdExistsException("Task for that account ID already exists");
    }

    public Task updateTask(String taskDescription, String accountId,
                           String taskName, String taskStatus){
        Optional<Account> user = accountRepository.findByAccountId(accountId);
        validateEntries(taskName,taskDescription);
        if (user.isEmpty())
            throw new AccountDoesNotExistException("Account with that Account ID does not exist");
        Optional<Task> task = taskRepository.getTaskByTaskId(user.get());

        if (task.isPresent()){
            Task updateTask = task.get();
            updateTask.setTaskDate(LocalDate.now());
            updateTask.setTaskDescription(taskDescription);
            updateTask.setTaskId(user.get());
            updateTask.setTaskName(taskName);
            updateTask.setTaskStatus(taskStatus);

            taskRepository.save(updateTask);
            return updateTask;
        }
        else throw new TaskIdDoesNotExistException("Task for that user ID does not exist");
    }

    public Task getTaskByUserId(String accountId){
        if (accountRepository.findByAccountId(accountId).isPresent()){
            Optional<Task> task =taskRepository.getTaskByTaskId(accountRepository.findByAccountId(accountId).get());
            return task.orElse(null);
        }
        throw new AccountDoesNotExistException("Account with that Account ID does not exist");

    }
    public Task deleteTask(String accountId) {
        Optional<Account> user = accountRepository.findByAccountId(accountId);
        if (user.isEmpty())
            throw new AccountDoesNotExistException("Account with Account ID does not exist");
        Optional<Task> task = taskRepository.getTaskByTaskId(user.get());
        if (task.isEmpty())
            throw new TaskIdDoesNotExistException("Task for that account ID does not exist");
        taskRepository.delete(task.get());
        return task.get();

    }
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


