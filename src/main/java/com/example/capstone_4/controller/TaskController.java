package com.example.capstone_4.controller;

import com.example.capstone_4.model.Task;
import com.example.capstone_4.service.TaskService;
import com.example.capstone_4.service.AdminTasksService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks") //same lang here, remember the syntax ehh
@RequiredArgsConstructor
public class TaskController {
    private final AdminTasksService adminTaskService;
    private final TaskService taskService;
    // GET /api/tasks
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(adminTaskService.getAllTasks());
    }

    // GET /api/tasks/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return adminTaskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/tasks
    @PostMapping
    public ResponseEntity<Task> createNewTask(@RequestBody Task task) {
        Task createdTask = adminTaskService.createNewTask(task);
        return ResponseEntity.ok(createdTask);
    }

    // PUT /api/tasks/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTaskById(@PathVariable Long id, @RequestBody Task task) {
        return adminTaskService.updateTaskById(id, task)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/tasks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        if (adminTaskService.deleteTaskById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
