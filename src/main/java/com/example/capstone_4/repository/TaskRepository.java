package com.example.capstone_4.repository;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>{

    Task findTopByOrderByIdDesc();

    List<Task> findByTaskId(Account account);

    Optional<List<Task>> getTaskByTaskId(Account account);
}
