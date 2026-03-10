package com.example.capstone_4.EndpointsTests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Tests all the invalid entries
 * */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class InvalidTasksTests extends Tests {

    @Test
    public void testGetTaskByNonExistingId() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/tasks/"+ String.valueOf(23L)))
                .andExpect(status().isNotFound())//404
                .andReturn();
    }

    @Test
    //@Rollback(false) // for checking
    public void testCreatedTaskMissingTitle() throws Exception{
        Map<String, String> successTask = new HashMap<>();
        successTask.put("taskDescription","A task description");
        successTask.put("taskStatus","In progress");
        successTask.put("taskDate",String.valueOf(LocalDate.now()));


        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(successTask);
        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .param("accountId", userAccountTest.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest()) //400
                .andReturn();


    }

    @Test
    void testUpdateNotFoundId()throws Exception{
        Map<String, String> successTask = new HashMap<>();
        successTask.put("taskName","EditedTask");
        successTask.put("taskDescription","A task description");
        successTask.put("taskStatus","In progress");
        successTask.put("taskDate",String.valueOf(LocalDate.now()));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(successTask);
        MvcResult result = mockMvc.perform(put("/api/tasks/"+(9999L))
                        .param("accountId", userAccountTest.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andReturn();
    }
    @Test
    public void testDeleteUnknowId() throws Exception{
        MvcResult result = mockMvc.perform(delete("/api/tasks/"+(99999999L)))
                .andExpect(status().isNotFound()) //404
                .andReturn();
    }

    @Test
    //@Rollback(false) // for checking
    public void testCreateExcessiveDescription() throws Exception{
        Map<String, String> successTask = new HashMap<>();
        successTask.put("taskName","A task");
        successTask.put("taskDescription","What is Lorem Ipsum?\n" +
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.");
        successTask.put("taskStatus","In progress");
        successTask.put("taskDate",String.valueOf(LocalDate.now()));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(successTask);
        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .param("accountId", userAccountTest.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Task description is too long. Max 200 characters allowed"))

                .andReturn();

    }
    @Test
    //@Rollback(false) // for checking
    public void testCreatedExcessiveTitle() throws Exception{
        Map<String, String> successTask = new HashMap<>();
        successTask.put("taskName","It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English.");
        successTask.put("taskDescription","A task description");
        successTask.put("taskStatus","In progress");
        successTask.put("taskDate",String.valueOf(LocalDate.now()));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(successTask);
        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .param("accountId", userAccountTest.getAccountId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Task name is too long. Max 60 characters allowed"))
                .andReturn();

    }

}
