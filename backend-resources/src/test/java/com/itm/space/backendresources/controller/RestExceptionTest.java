package com.itm.space.backendresources.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class RestExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

/*    @Test
    public void testHandleBackendResourcesException() throws Exception {
        mockMvc.perform(get("/api/test/exception"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Test exception"));
    }

    @Test
    public void testHandleInvalidArgument() throws Exception {
        mockMvc.perform(post("/api/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TestController.TestBody())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("must not be null"));
    }*/
}
