package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private ItemRequestDto itemRequest1Dto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        User user1 = new User(1L, "User1 name", "user1@mail.com");

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("ItemRequest1 description")
                .requester(user1)
                .created(now)
                .build();
        itemRequest1Dto = ItemRequestMapper.toItemRequestDto(itemRequest1);

        Item item1 = Item.builder()
                .id(1L)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .itemRequest(itemRequest1)
                .build();
        ItemDto item1Dto = ItemMapper.toItemDto(item1);

        itemRequest1Dto.setItems(List.of(item1Dto));
    }

    @Test
    void serializeTest() throws Exception {
        JsonContent<ItemRequestDto> response = json.write(itemRequest1Dto);

        Integer id = Math.toIntExact(itemRequest1Dto.getId());
        Integer requesterId = Math.toIntExact(itemRequest1Dto.getRequesterId());

        assertThat(response).hasJsonPath("$.id");
        assertThat(response).hasJsonPath("$.requesterId");
        assertThat(response).hasJsonPath("$.description");
        assertThat(response).hasJsonPath("$.created");
        assertThat(response).hasJsonPath("$.items");

        assertThat(response).extractingJsonPathNumberValue("$.id").isEqualTo(id);
        assertThat(response).extractingJsonPathNumberValue("$.requesterId").isEqualTo(requesterId);
        assertThat(response).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequest1Dto.getDescription());
        assertThat(response).extractingJsonPathArrayValue("$.items").isNotEmpty();
    }
}
