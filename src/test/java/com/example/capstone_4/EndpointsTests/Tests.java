package com.example.capstone_4.EndpointsTests;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import com.example.capstone_4.repository.AccountRepository;
import com.example.capstone_4.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

public class Tests {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected TaskRepository taskRepository;
    @Autowired
    protected AccountRepository accountRepository;

    protected Account adminAccountTest;
    protected Account userAccountTest;
    protected UserDetails mockUserDetails1;
    protected UserDetails mockUserDetails2;
    protected Task sampleTask;

    @BeforeEach
    public void setUp(){
        userAccountTest =new Account();
        userAccountTest.setId(1L);
        userAccountTest.setAccountId("ID101");
        userAccountTest.setUsername("random");
        userAccountTest.setPassword("password");
        userAccountTest.setRole("ROLE_USER");
        accountRepository.save(userAccountTest);
        this.mockUserDetails1 = org.springframework.security.core.userdetails.User
                .withUsername("random")
                .password("password") // doesn't really matter
                .authorities("ROLE_USER")
                .build();

        sampleTask = new Task();
        sampleTask.setTaskDescription("Item");
        sampleTask.setTaskDate(LocalDate.now());
        sampleTask.setTaskName("A task");
        sampleTask.setTaskStatus("In Progress");
        sampleTask.setTaskId(userAccountTest);

        taskRepository.save(sampleTask);

        /*
        adminAccountTest.setPassword("password");
        adminAccountTest.setRole("ROLE_ADMIN");
        this.mockUserDetails2 = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password("password") // doesn't really matter
                .authorities("ROLE_ADMIN")
                .build();

         */
    }
}
