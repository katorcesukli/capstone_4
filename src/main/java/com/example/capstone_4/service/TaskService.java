package com.example.capstone_4.service;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import com.example.capstone_4.repository.TaskRepository;

public class TaskService {

    private TaskRepository taskRepository;

    //method to generate the next formatted Task ID string (e.g., 0001, 0002)
    private String generateNextAccountId() {
        Task lastTask = taskRepository.findTopByOrderByIdDesc();
        long nextId = (lastTask != null) ? lastTask.getId() + 1 : 1;
        return String.format("%04d", nextId);
    }
}
