package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;

@WebMvcTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class itemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String HEADER = "X-Sharer-User-Id";

    User user = new User(
            1L,
            "someName",
            "email@mail.com");

    ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "someDescription",
            1L,
            LocalDateTime.now(),
            null);


    @Test
    void create() throws Exception {
        when(itemRequestService.create(anyLong(), any())).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HEADER, 1L)
                .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requesterId").value(1L))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void getRequestsInfoTest() throws Exception {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        ItemRequestDto req = ItemRequestMapper.toItemRequestDto(itemRequest);
        when(itemRequestService.getOnwRequests(anyLong())).thenReturn(Collections.singletonList(req));

        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].requesterId").value(1L))
                .andExpect(jsonPath("$[0].description").value("description"));
    }
}
