package com.example.capstone_4.controller;

import com.example.capstone_4.Exceptions.AccountDoesNotExistException;
import com.example.capstone_4.Exceptions.ExcessiveLengthException;
import com.example.capstone_4.Exceptions.MissingRequiredFieldException;
import com.example.capstone_4.model.Task;
import com.example.capstone_4.service.TaskService;
import com.example.capstone_4.service.AdminTasksService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
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

    @GetMapping("/id/{stringTaskId}")
    public List<Task> getTaskByStringTaskId(@PathVariable String stringTaskId) {
        return taskService.getTaskByStringTaskId(stringTaskId);
    }



    // POST /api/tasks
    @PostMapping
    public ResponseEntity<?> createNewTask(@RequestBody Task task,@RequestParam String accountId) {
        // accountId is included in the task JSON from frontend
        try{
            Task createdTask = taskService.createNewTask(task, accountId);

            return ResponseEntity.status(201).body(createdTask);

        } catch (Exception e) {
            if (e instanceof MissingRequiredFieldException || e instanceof ExcessiveLengthException)
                return ResponseEntity.status(400).body("Error: "+ e.getMessage());
            else
                return ResponseEntity.status(500).body("Error: "+e.getMessage());

        }
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
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /*** User accessible Controllers***/

    //for now "/api/tasks/user
    @GetMapping("/user/{accountId}")
    public ResponseEntity<?> getAllUserTask(@PathVariable("accountId")String accountId){
        try{
            return ResponseEntity.ok(taskService.getByTaskByUserId(accountId));

        }catch (Exception e){
            if (e instanceof AccountDoesNotExistException){
                return ResponseEntity.status(404).body("Error: "+e.getMessage());
            }else
                return ResponseEntity.status(500).body("Error: "+e.getMessage());
        }
    }

    @PostMapping("/user/{accountId}")
    public ResponseEntity<?> createTask(@PathVariable("accountId")String accountId, @RequestBody Task task){
        try{
            return ResponseEntity.status(201).body(taskService.createTask(accountId, task));

        }catch (Exception e){
            if (e instanceof AccountDoesNotExistException){
                return ResponseEntity.status(404).body("Error: "+e.getMessage());
            }else
                return ResponseEntity.status(500).body("Error: "+e.getMessage());
        }
    }


}
