package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        User user = new User(1L, "SomeName", "someMail@mail.com");
        userDto = UserMapper.toUserDto(user);
    }

    @Test
    void serializeTest() throws Exception {
        JsonContent<UserDto> result = json.write(userDto);
        Integer value = Math.toIntExact(userDto.getId());

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathNumberValue(
                "$.id").isEqualTo(value);
        assertThat(result).extractingJsonPathStringValue(
                "$.name").isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathStringValue(
                "$.email").isEqualTo(userDto.getEmail());
    }
}
