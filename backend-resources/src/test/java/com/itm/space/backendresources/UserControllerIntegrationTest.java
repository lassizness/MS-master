package com.itm.space.backendresources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.exception.BackendResourcesException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.UUID;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private Keycloak keycloakClient; // Мокирование клиента Keycloak для имитации взаимодействия с Keycloak
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Тестирование создания пользователя")
    @WithMockUser(roles = "MODERATOR")
    public void testCreateUser_Success() throws Exception {
        // Инициализируем тестовый запрос для создания пользователя
        UserRequest userRequest = new UserRequest("valera2", "mamgja2@mail.ru", "3444", "firstValera2", "lastValera2");

        // Мокирование ответа от Keycloak: создаем мок объект UsersResource, который будет имитировать создание пользователя в Keycloak
        UsersResource usersResourceMock = mock(UsersResource.class);
        when(usersResourceMock.create(any(UserRepresentation.class))).thenReturn(Response.created(null).build()); // Указываем, что при вызове метода create будет возвращаться успешный ответ

        // Мокирование RealmResource: создаем мок объект, который будет возвращать мокированный UsersResource при вызове метода users()
        RealmResource realmResourceMock = mock(RealmResource.class);
        when(realmResourceMock.users()).thenReturn(usersResourceMock);

        // Указываем, что при обращении к Keycloak и вызове метода realm() будет возвращаться мок объект realmResourceMock
        when(keycloakClient.realm(anyString())).thenReturn(realmResourceMock);

        // Выполняем запрос на создание пользователя и проверяем, что ответ успешен
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk()); // Ожидаем статус OK (200) при успешном создании пользователя
    }

    @Test
    @DisplayName("Простой тест на проверку возвращаемого значения при обращении к эндпоинту \"/api/users/hello\"")
    @WithMockUser(username = "valera", roles = "MODERATOR")
    void testHello() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("valera")); // Ожидаем, что ответ будет равен имени пользователя
    }

    // Тестирование создания пользователя с недостающими данными
    @Test
    @DisplayName("Тестирование создания пользователя с недостающими данными")
    @WithMockUser(roles = "MODERATOR")
    public void testCreateUser_ExeptionOne() throws Exception {
        UserRequest userRequest = new UserRequest(null, "mamgja@mail.ru", "3444", "firstValera", "lastValera");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest()); // Ожидаем статус ошибки из-за некомплектных данных
    }

    // Тестирование создания пользователя с пустым полем email
    @Test
    @DisplayName("Тестирование создания пользователя с пустым полем email")
    @WithMockUser(roles = "MODERATOR")
    public void testCreateUser_ExeptionTwo() throws Exception {
        UserRequest userRequest = new UserRequest("u", "", "p", "f", "l");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest()); // Ожидаем статус ошибки из-за пустого email
    }

    // Тестирование успешного получения информации о пользователе
    @Test
    @DisplayName("Тестирование успешного получения информации о пользователе")
    @WithMockUser(roles = "MODERATOR")
    public void testGetUserById_Success() throws Exception {
        UUID userId = UUID.fromString("1f4ce7e5-67bf-4231-a934-738cd378abdc");

        RealmResource realmResource = mock(RealmResource.class);
        UsersResource usersResource = mock(UsersResource.class);
        UserResource userResource = mock(UserResource.class);
        RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);

        UserRepresentation mockUser = new UserRepresentation();
        mockUser.setFirstName("firstValera");
        mockUser.setLastName("lastValera");
        mockUser.setEmail("mamgja@mail.ru");

        // Настройка моков
        when(keycloakClient.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(String.valueOf(userId))).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(mockUser);

        // Мокирование ролей
        RoleRepresentation mockRole = new RoleRepresentation();
        mockRole.setName("MODERATOR");
        MappingsRepresentation mockMappings = new MappingsRepresentation();
        mockMappings.setRealmMappings(Collections.singletonList(mockRole));

        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.getAll()).thenReturn(mockMappings);

        // Выполнение запроса и проверка
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("firstValera"))
                .andExpect(jsonPath("$.lastName").value("lastValera"))
                .andExpect(jsonPath("$.email").value("mamgja@mail.ru"))
                .andExpect(jsonPath("$.roles[0]").value("MODERATOR"));
    }

    @Test
    @DisplayName("Тестирование сценария, когда пользователь не найден")
    @WithMockUser(roles = "MODERATOR")
    public void testGetUserById_UserNotFound() throws Exception {
        UUID userId = UUID.fromString("1f4ce7e5-67bf-4231-a934-738cd378abdc");

        RealmResource realmResource = mock(RealmResource.class);
        UsersResource usersResource = mock(UsersResource.class);

        // Настройка моков
        when(keycloakClient.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(String.valueOf(userId))).thenThrow(new BackendResourcesException("Пользователь с ID " + userId + " не найден", HttpStatus.NOT_FOUND));

        // Выполнение запроса и проверка
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", userId))
                .andExpect(status().isNotFound()); // Ожидаем статус 404 (Пользователь не найден)
    }

}