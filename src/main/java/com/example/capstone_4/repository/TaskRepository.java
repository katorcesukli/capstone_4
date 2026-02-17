package com.example.capstone_4.repository;

import com.example.capstone_4.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Account, Long>{

}
