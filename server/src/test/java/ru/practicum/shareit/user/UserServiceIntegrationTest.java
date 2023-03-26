package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final UserService userService;

    @Test
    void getAllUsers() {
        UserDto firstUserDTO = UserDto.builder()
                .name("First User")
                .email("firstuser@yandex.ru")
                .build();

        UserDto secondUserDTO = UserDto.builder()
                .name("Second User")
                .email("seconduser@yandex.ru")
                .build();

        UserDto first = userService.create(firstUserDTO);
        UserDto second = userService.create(secondUserDTO);
        List<UserDto> userDTOs = userService.getAll();

        assertEquals(first.getId(), 1L);
        assertEquals(second.getId(), 2L);
        assertEquals(userDTOs.size(), 2);
    }
}
