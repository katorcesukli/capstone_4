package com.example.capstone_4.EndpointsTests;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;


import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class TasksTests extends Tests{


    @Test
    //@Rollback(false) // for checking
    public void testCreatedTaskSuccessful() throws Exception{
        Map<String, String> successTask = new HashMap<>();
        successTask.put("taskName","A task");
        successTask.put("taskDescription","A task description");
        successTask.put("taskStatus","In progress");
        successTask.put("taskDate",String.valueOf(LocalDate.now()));


        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(successTask);
        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .param("accountId", userAccountTest.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isCreated())//201
                .andReturn();

    }

    @Test
    public void testGetTaskSuccessful() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())//200
                .andReturn();
    }
    @Test
    public void testGetTaskByIdSuccessful() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/tasks/"+sampleTask.getId()))
                .andExpect(status().isOk())//200
                .andReturn();
    }


    @Test
    public void testDeleteTaskSuccessful() throws Exception{
        MvcResult result = mockMvc.perform(delete("/api/tasks/"+sampleTask.getId()))
                .andExpect(status().isNoContent()) //204
                .andReturn();
    }

    @Test
    void testUpdateTaskSuccessful()throws Exception{
        Map<String, String> successTask = new HashMap<>();
        successTask.put("taskName","EditedTask");
        successTask.put("taskDescription","A task description");
        successTask.put("taskStatus","In progress");
        successTask.put("taskDate",String.valueOf(LocalDate.now()));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(successTask);
        MvcResult result = mockMvc.perform(put("/api/tasks/"+sampleTask.getId())
                        .param("accountId", userAccountTest.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())//200
                .andReturn();
    }

}
