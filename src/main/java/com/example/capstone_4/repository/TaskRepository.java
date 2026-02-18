package com.example.capstone_4.repository;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>{

    //for auto formatting taskId
    Task findTopByOrderByIdDesc();

    Optional<Task> findByTaskId(String taskId);
}
