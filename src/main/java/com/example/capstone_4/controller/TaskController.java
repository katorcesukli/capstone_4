package com.example.capstone_4.controller;

import com.example.capstone_4.model.Task;
import com.example.capstone_4.model.Account;
import com.example.capstone_4.service.AccountService;
import com.example.capstone_4.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks") //same lang here, remember the syntax ehh
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final AccountService accountService;

    // GET /api/tasks
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // GET /api/tasks/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/tasks
    @PostMapping
    public ResponseEntity<Task> createNewTask(@RequestBody Task task,@RequestParam String accountId) {
        // accountId is included in the task JSON from frontend
        Task createdTask = taskService.createNewTask(task, accountId);
        return ResponseEntity.ok(createdTask);
    }

    // PUT /api/tasks/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTaskById(@PathVariable Long id, @RequestBody Task task) {
        return taskService.updateTaskById(id, task)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/tasks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        if (taskService.deleteTaskById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }


}
