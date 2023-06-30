package com.itm.space.backendresources.service;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.exception.BackendResourcesException;
import com.itm.space.backendresources.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private Keycloak keycloakClient;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;

    @Mock
    private RealmResource realmResource;

    @BeforeEach
    void setup() {
        userService.realm = "ITM"; // Устанавливается значение realm в "ITM"
        when(keycloakClient.realm(userService.realm)).thenReturn(realmResource); // Мокируется метод realm() для возвращения realmResource
        when(realmResource.users()).thenReturn(usersResource); // Мокируется метод users() для возвращения usersResource
    }

    @Test
    void createUserTest() {
        UserRequest userRequest = new UserRequest("valera", "mamgja@mail.ru", "test", "gg", "gg");
        UserRepresentation userRepresentation = new UserRepresentation();
        Response response = Response.created(null).build();

        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response); // Мокируется метод create() для возвращения response

        assertDoesNotThrow(() -> userService.createUser(userRequest)); // Проверяется, что вызов метода createUser() не вызывает исключения

        verify(usersResource, times(1)).create(any(UserRepresentation.class)); // Проверяется, что метод create() вызывается ровно 1 раз
    }

    @Test
    void createUserTestFailure() {
        UserRequest userRequest = new UserRequest("valera", "mamgja@mail.ru", "test", "gg", "gg");
        Response response = Response.serverError().build();
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response); // Мокируется метод create() для возвращения response с ошибкой

        assertThrows(BackendResourcesException.class, () -> userService.createUser(userRequest)); // Проверяется, что вызов метода createUser() вызывает BackendResourcesException

        // Проверка завершена успешно, так как ожидается исключение
    }

    @Test
    void getUserByIdTest() {
        UUID userId = UUID.randomUUID();
        UserRepresentation userRepresentation = new UserRepresentation();
        List<UserRepresentation> userList = Collections.singletonList(userRepresentation);
        when(usersResource.get(userId.toString())).thenReturn(userResource); // Мокируется метод get() для возвращения userResource
        when(userResource.toRepresentation()).thenReturn(userRepresentation); // Мокируется метод toRepresentation() для возвращения userRepresentation
        when(userResource.roles()).thenReturn(null); // Мокируется метод roles() для возвращения null

        assertThrows(BackendResourcesException.class, () -> userService.getUserById(userId)); // Проверяется, что вызов метода getUserById() вызывает BackendResourcesException

        verify(usersResource, times(1)).get(userId.toString()); // Проверяется, что метод get() вызывается ровно 1 раз
        verify(userResource, times(1)).toRepresentation(); // Проверяется, что метод toRepresentation() вызывается ровно 1 раз
        verify(userResource, times(1)).roles(); // Проверяется, что метод roles() вызывается ровно 1 раз
    }
}
