package com.itm.space.backendresources.service;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.exception.BackendResourcesException;
import com.itm.space.backendresources.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;


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
    private UserResponse userResponse;
    @Mock
    private UserRequest userRequest;

    @Mock
    private RealmResource realmResource;
    @Mock
    private RoleMappingResource rolesResource;

    private UUID id;
    private UserRepresentation userRepresentation;

    @BeforeEach
    void setup() {
        userService.realm = "ITM";
        when(keycloakClient.realm(userService.realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        userRequest = new UserRequest("valera", "mamgja@mail.ru", "3444", "firstValera", "lastValera");
        id = UUID.randomUUID();
        userResponse = new UserResponse("firstValera", "lastValera", "mamgja@mail.ru", List.of("MODERATOR"), List.of("MODERATOR"));
    }

    @Test
    public void createUser_Success() {
        // Допустимые значения
        UserRequest userRequest = new UserRequest("valera", "mamgja@mail.ru", "test", "gg", "gg");
        id = UUID.randomUUID();

        // Мокируем метод usersResource.create() и возвращаем заглушку
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(Response.created(null).build());

        // Вызов метода
        userService.createUser(userRequest);

        // Проверка взаимодействий
        verify(usersResource, times(1)).create(any(UserRepresentation.class));
    }

    @Test
    public void createUser_WebApplicationExceptionThrown() {
        // Подготовка входных данных
        UserRequest userRequest = new UserRequest("valera", "mamgja@mail.ru", "test", "gg", "gg");

        // Настройка моков
        when(usersResource.create(any(UserRepresentation.class)))
                .thenThrow(new WebApplicationException("Test exception"));

        // Ожидаем исключение и проверяем его
        BackendResourcesException thrownException = assertThrows(BackendResourcesException.class,
                () -> userService.createUser(userRequest));

        assertTrue(thrownException.getMessage().contains("Test exception"));

        // Проверка, что моки действительно вызываются
        verify(usersResource).create(any(UserRepresentation.class));
    }

    @Test
    public void getUserById_Success() {
        UUID id = UUID.fromString("7b745b1b-034e-409a-9ff8-e8778a8497d3");

        // Создаем тестовые данные для UserResponse
        UserResponse userResponse = new UserResponse("firstValera", "lastValera", "mamgja@mail.ru", Collections.singletonList("MODERATOR"), Collections.singletonList("GROUP1"));

        // Создаем тестовые данные для UserRepresentation
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername("valera");
        userRepresentation.setEmail("mamgja@mail.ru");
        userRepresentation.setFirstName("firstValera");
        userRepresentation.setLastName("lastValera");

        // Создаем тестовые данные для ролей и групп
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("MODERATOR");
        List<RoleRepresentation> roles = Collections.singletonList(roleRepresentation);

        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName("GROUP1");
        List<GroupRepresentation> groups = Collections.singletonList(groupRepresentation);

        MappingsRepresentation mappingsRepresentation = new MappingsRepresentation();
        mappingsRepresentation.setRealmMappings(roles);

        // Мокируем методы
        when(usersResource.get(String.valueOf(id))).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);
        when(userResource.roles()).thenReturn(rolesResource);
        when(rolesResource.getAll()).thenReturn(mappingsRepresentation);
        when(userResource.groups()).thenReturn(groups);

        // Мокируем метод userMapper.userRepresentationToUserResponse() для возврата нужных данных
        when(userMapper.userRepresentationToUserResponse(userRepresentation, roles, groups)).thenReturn(userResponse);

        // Вызываем тестируемый метод
        UserResponse result = userService.getUserById(id);

        // Проверяем результат
        assertNotNull(result);
        assertEquals(userResponse.getEmail(), result.getEmail());
        assertEquals(userResponse.getFirstName(), result.getFirstName());
        assertEquals(userResponse.getLastName(), result.getLastName());
        assertEquals(userResponse.getRoles(), result.getRoles());
        assertEquals(userResponse.getGroups(), result.getGroups());

        // Проверка, что моки действительно вызываются
        verify(usersResource).get(id.toString());
        verify(userResource).toRepresentation();
        verify(userResource,times(2)).roles();
        verify(rolesResource,times(2)).getAll();
        verify(userResource).groups();
        verify(userMapper).userRepresentationToUserResponse(userRepresentation, roles, groups);
        verifyNoMoreInteractions(userResource); // Убеждаемся, что не было дополнительных вызовов
    }

}
