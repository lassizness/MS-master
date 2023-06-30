package com.itm.space.backendresources.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserRequest userRequest;
    private UserResponse userResponse;
    private UUID id;

   /* @BeforeEach
    void setUp() {
        userRequest = new UserRequest("valera","mamgja@mail.ru","3444","ne","valera");
        id = UUID.randomUUID();
        userResponse = new UserResponse("valera","mamgja@mail.ru","3444","ne","valera");

        when(userService.createUser(userRequest)).thenReturn(null); // Adjust if your method returns something
        when(userService.getUserById(id)).thenReturn(userResponse);
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void testCreateUser() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/" + id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.getId().toString()));
    }
*/
    @Test
    @WithMockUser(username = "test", roles = "MODERATOR")
    void testHello() throws Exception {
        mockMvc.perform(get("/api/users/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("test"));
    }
}
