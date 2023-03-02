package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    private User user1;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "User1 name", "user1@mail.com");
    }

    @Test
    void createUserTest() {
        when(repository.save(any(User.class))).thenReturn(user1);

        UserDto userDto = service.create(
                UserMapper.toUserDto(user1));

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }


    @Test
    void updateUserWithEmailFormatTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(repository.save(any(User.class)))
                .thenReturn(user1);

        UserDto userDto = UserMapper.toUserDto(user1);
        service.update(userDto.getId(), userDto);

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void updateUserWithNoUser() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        UserDto userDto = UserMapper.toUserDto(user1);
        userDto.setId(10L);
        NotFoundException exc = assertThrows(NotFoundException.class,
                () -> service.update(1L, userDto)
        );

        assertEquals("Пользователя с id 1 несуществует", exc.getMessage());
    }

    @Test
    void getAllUsersWhenUserFoundThenReturnedUser() {

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(repository.save(any(User.class)))
                .thenReturn(user1);

        UserDto userDto = UserMapper.toUserDto(user1);
        service.update(userDto.getId(), userDto);

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void getAllUsersWhenUserFoundThenUserNotFoundExceptionThrown() {
        long userId = 0L;
        //    User expectedUser = new User();
        when(repository.findById(userId))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getById(userId));
    }

    @Test
    void getByIdTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        UserDto userDto = service.getById(user1.getId());

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void getUserWrongIdTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getById(user1.getId()));

        assertEquals("Пользователя с id 1 несуществует", exception.getMessage());
    }



    @Test
    void deleteUserTestWithNoUser() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        UserDto userDto = UserMapper.toUserDto(user1);
        userDto.setId(10L);
        NotFoundException exc = assertThrows(NotFoundException.class,
                () -> service.delete(1L)
        );

        assertEquals("Пользователя с id 1 несуществует", exc.getMessage());
    }

    @Test
    void getAllUsersTest() {
        when(repository.findAll())
                .thenReturn(List.of(user1));

        List<UserDto> userDto = service.getAll();

        assertEquals(1, userDto.size());
        assertEquals(1, userDto.get(0).getId());
        assertEquals("User1 name", userDto.get(0).getName());
        assertEquals("user1@mail.com", userDto.get(0).getEmail());
    }
}
