package com.example.capstone_4.EndpointsTests;

import com.example.capstone_4.model.Account;
import com.example.capstone_4.model.Task;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonResponse);
        Assertions.assertTrue(jsonNode.isArray(), "Expected JSON array");

    }
    @Test
    public void testGetTaskByIdSuccessful() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/tasks/"+sampleTask.getId()))
                .andExpect(status().isOk())//200
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Task task= mapper.readValue(jsonResponse,Task.class);

        assertEquals(sampleTask.getTaskName(),task.getTaskName());
        assertEquals(sampleTask.getTaskDescription(),task.getTaskDescription());
        assertEquals(sampleTask.getTaskId(),task.getTaskId());
        assertEquals(sampleTask.getTaskStatus(),task.getTaskStatus());
        assertEquals(sampleTask.getId(),task.getId());

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

    @Test
    public void getTasksAfterCreate() throws Exception{
        //Create
        LocalDate now =LocalDate.now();
        Map<String, String> successTask = new HashMap<>();
        successTask.put("taskName","A Created task");
        successTask.put("taskDescription","A task description");
        successTask.put("taskStatus","Not Started");
        successTask.put("taskDate",String.valueOf(now));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(successTask);
        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .param("accountId", userAccountTest.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isCreated())//201
                .andReturn();

        //GET
        MvcResult getResult = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.taskName == '%s' && @.taskDescription == '%s' && @.taskStatus =='%s' && @.taskDate=='%s')]",
                "A Created task", "A task description","Not Started",now.toString()).exists()).andReturn();




    }


}
