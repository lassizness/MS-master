package com.itm.space.backendresources.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void causeBackendExceptionTest() throws Exception {
        mockMvc.perform(get("/causeBackendException"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").value("Тест исключений"));
    }


    @Test
    public void causeMethodArgumentNotValidExceptionTest_EmptyParameter() throws Exception {
        mockMvc.perform(get("/causeMethodArgumentNotValidException")
                        .param("input", "")) // передаем пустой параметр "input"
                .andExpect(status().isBadRequest());
    }
}
