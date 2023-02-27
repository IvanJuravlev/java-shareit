package ru.practicum.shareit.Item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Comment.Comment;
import ru.practicum.shareit.item.Comment.CommentDto;
import ru.practicum.shareit.item.Comment.CommentMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemController;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CommentMapper commentMapper;

    private ItemMapper itemMapper;

    private UserDto userDto1;

    private UserDto userDto2;

    private ItemDto itemDto;

    private ItemBookingDto itemBookingDto;
    private CommentDto commentDto;

    @BeforeEach
//    void setUp() {
//        itemDto = new ItemDto(
//                1L,
//                "item",
//                "item description",
//                true,
//                1L,
//                null,
//                null,
//                null,
//                Collections.EMPTY_LIST
//        );
//        commentDto = new CommentDto(1L,
//                "Text comment",
//                "User1",
//                LocalDateTime.of(2020, 7, 8, 5, 4, 1));
//        dtoItems = new ArrayList<>();
//    }
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        User user1 = new User(1L, "UserName1", "user1@mail.ru");
        userDto1 = UserMapper.toUserDto(user1);

        User user2 = new User(1L, "UserName2", "user2@mail.ru");
        userDto2 = UserMapper.toUserDto(user2);

        Item item1 = Item.builder()
                .id(1L)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .itemRequest(null)
                .build();
        itemDto = ItemMapper.toItemDto(item1);
        itemBookingDto = ItemMapper.toItemBookingDto(item1);

        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Comment1 text")
                .item(item1)
                .author(user2)
                .created(now)
                .build();
        commentDto = CommentMapper.toCommentDto(comment1);
    }

    @Test
    void findAll() throws Exception {
        when(itemService.getAllByOwner(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemBookingDto));

        mockMvc.perform(get("/items")
                .content(objectMapper.writeValueAsString(itemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemBookingDto)));
    }

    @Test
    void create() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(itemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void update() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                .content(objectMapper.writeValueAsString(itemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void search() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                .param("text", "Item1")
                .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform((post("/items/1/comment"))
                .content(objectMapper.writeValueAsString(itemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }



}
