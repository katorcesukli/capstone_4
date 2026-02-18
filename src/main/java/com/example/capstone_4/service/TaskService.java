package com.example.capstone_4.service;

import com.example.capstone_4.Exceptions.AccountDoesNotExistException;
import com.example.capstone_4.Exceptions.TaskIdDoesNotExistException;
import com.example.capstone_4.Exceptions.TaskIdExistsException;
import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import com.example.capstone_4.repository.AccountRepository;
import com.example.capstone_4.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    public TaskService(TaskRepository taskRepository, AccountRepository accountRepository){
        this.taskRepository=taskRepository;
        this.accountRepository =accountRepository;
    }
    //Local date time is automatically localdate.now
    public Task createTask(String taskId,
                           String taskName, String taskStatus,String taskDescription) throws TaskIdExistsException {
        Optional<Account> user = accountRepository.findByAccountId(taskId);

        if (user.isEmpty())
            throw new AccountDoesNotExistException("Account with that Account ID does not exist");
        Optional<Task> task = taskRepository.getTaskByTaskId(user.get());

        if (task.isEmpty()){
            Task newTask=new Task();
            newTask.setTaskDate(LocalDate.now());
            newTask.setTaskDescription(taskDescription);
            newTask.setTaskId(taskId);
            newTask.setTaskName(taskName);
            newTask.setTaskStatus(taskStatus);

            taskRepository.save(newTask);
            return newTask;
        }
        else throw new TaskIdExistsException("Task with that ID already exists");
    }
    //User entered a date
    public Task createTask(LocalDate taskDate, String taskId,
                           String taskName, String taskStatus,String taskDescription){
        Optional<Account> user = accountRepository.findByAccountId(taskId);
        if (user.isEmpty())
            throw new AccountDoesNotExistException("Account with that Account ID does not exist");
        Optional<Task> task = taskRepository.getTaskByTaskId(user.get());

        if (task.isEmpty()){
            Task newTask=new Task();
            newTask.setTaskDate(taskDate);
            newTask.setTaskDescription(taskDescription);
            newTask.setTaskId(taskId);
            newTask.setTaskName(taskName);
            newTask.setTaskStatus(taskStatus);

            taskRepository.save(newTask);
            return newTask;
        }
        else throw new TaskIdExistsException("Task with that ID already exists");
    }

    public Task updateTask(String taskDescription, String taskId,
                           String taskName, String taskStatus){
        Optional<Account> user = accountRepository.findByAccountId(taskId);
        if (user.isEmpty())
            throw new AccountDoesNotExistException("Account with that Account ID does not exist");
        Optional<Task> task = taskRepository.getTaskByTaskId(user.get());

        if (task.isPresent()){
            Task updateTask = task.get();
            updateTask.setTaskDate(LocalDate.now());
            updateTask.setTaskDescription(taskDescription);
            updateTask.setTaskId(taskId);
            updateTask.setTaskName(taskName);
            updateTask.setTaskStatus(taskStatus);

            taskRepository.save(updateTask);
            return updateTask;
        }
        else throw new TaskIdDoesNotExistException("Task with that ID does not exist");
    }

    public Task getByTaskByUserId(String userId){
        if (accountRepository.findByAccountId(userId).isPresent()){
            Optional<Task> task =taskRepository.getTaskByTaskId(accountRepository.findByAccountId(userId).get());
            return task.orElse(null);
        }
        throw new AccountDoesNotExistException("Account with that Account ID does not exist");

    }
    public Task deleteTask(String username,String taskDescription, String taskId,
                           String taskName, String taskStatus) {
        Optional<Account> user = accountRepository.findByAccountId(taskId);
        if (user.isEmpty())
            throw new AccountDoesNotExistException("Account with Account ID does not exist");
        Optional<Task> task = taskRepository.getTaskByTaskId(user.get());
        if (task.isEmpty())
            throw new TaskIdDoesNotExistException("Task with that ID does not exist");
        taskRepository.delete(task.get());
        return task.get();

    }


}