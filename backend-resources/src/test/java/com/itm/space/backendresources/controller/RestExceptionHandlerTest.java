package com.itm.space.backendresources.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@SpringBootTest
@AutoConfigureMockMvc
public class RestExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private UserService userService;
    private UserRequest userRequest;
    private UserResponse userResponse;
    private UUID id;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("valera2", "mamgja2@mail.ru", "3444", "firstValera2", "lastValera2");
        id = UUID.randomUUID();
        userResponse = new UserResponse("firstValera", "lastValera", "mamgja@mail.ru", List.of("MODERATOR"), List.of("MODERATOR"));

       /* doNothing().when(userService).createUser(userRequest);
        when(userService.getUserById(id)).thenReturn(userResponse);*/
    }

    @Test
    @WithMockUser(roles = "MODERATOR") // Аннотация для аутентификации пользователя с ролью "MODERATOR"
    public void testCreateUserNull() throws Exception {
        // Подготовка данных для запроса с нулевым именем
        UserRequest userRequest = new UserRequest(null, "mamgja@mail.ru", "test", "gg", "gg");

        // Выполнение POST-запроса на создание пользователя
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest()); // Ожидание статуса "Bad Request" (400)
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void testCreateUserException() throws Exception {
        // Настройка мок сервиса, чтобы он выбрасывал исключение с запрошенным статусом
        doThrow(new WebApplicationException(Response.Status.FORBIDDEN)).when(userService).createUser(userRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(status().isForbidden());
    }

    /*    @Test
    public void testHandleException() {
        // Создание исключения с заданными параметрами
        BackendResourcesException exception = new BackendResourcesException("Test Exception", HttpStatus.INTERNAL_SERVER_ERROR);

        // Создание контроллера и вызов метода handleException
        RestExceptionHandler handler = new RestExceptionHandler();
        ResponseEntity<String> responseEntity = handler.handleException(exception);

        // Проверка статуса и сообщения в ответе
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Test Exception", responseEntity.getBody());
    }*/
    /*    @Test
    @WithMockUser(roles = "MODERATOR") // Аннотация для аутентификации пользователя с ролью "MODERATOR"
    public void getUserById_Success() {
        UUID userId = UUID.fromString("7b745b1b-034e-409a-9ff8-e8778a8497d3");

        // Ожидаемый ответ
        UserResponse expectedResponse = new UserResponse("firstValera", "lastValera", "mamgja@mail.ru", Collections.singletonList("MODERATOR"), Collections.singletonList("GROUP1"));

        // Настройка мока, чтобы возвращал ожидаемый ответ при вызове метода getUserById
        when(userService.getUserById(userId)).thenReturn(expectedResponse);

        // Вызов метода контроллера для получения пользователя по ID
        UserResponse result = userController.getUserById(userId);

        // Проверки
        assertNotNull(result);
        assertEquals(expectedResponse.getEmail(), result.getEmail());
        assertEquals(expectedResponse.getFirstName(), result.getFirstName());
        assertEquals(expectedResponse.getLastName(), result.getLastName());

        // Проверка, что метод userService.getUserById(userId) был вызван
        verify(userService).getUserById(userId);
    }*/
}