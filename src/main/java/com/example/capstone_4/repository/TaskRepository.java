package com.example.capstone_4.repository;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>{

    Task findTopByOrderByIdDesc();

    //@Query("SELECT t FROM Task t ORDER BY t.taskDate ASC")
    List<Task> findByTaskIdOrderByTaskDate(Account account);
    List<Task> findByTaskId(Account account);
    Optional<List<Task>> getTaskByTaskId(Account account);

    @Query("SELECT t FROM Task t ORDER BY t.taskDate ASC")
    List<Task> findAll();

}
