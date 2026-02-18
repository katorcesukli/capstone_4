package com.example.capstone_4.service;


import com.example.capstone_4.model.Task;
import com.example.capstone_4.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

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
}
