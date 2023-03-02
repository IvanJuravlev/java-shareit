package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private UserDto user2Dto;
    private BookingDto booking1Dto;
    private BookingDto booking1DtoResponse;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        User user1 = new User(1L, "User1 name", "user1@mail.com");
        User user2 = new User(2L, "User2 name", "user2@mail.com");
        user2Dto = UserMapper.toUserDto(user2);

        Item item1 = Item.builder()
                .id(1L)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .itemRequest(null)
                .build();

        Booking booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        booking1Dto = BookingMapper.toBookingDto(booking1);
        booking1DtoResponse = BookingMapper.toBookingDto(booking1);
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(anyLong(), any(ShortBookingDto.class)))
                .thenReturn(booking1DtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .content(mapper.writeValueAsString(booking1Dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, user2Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(booking1DtoResponse)));

    }


    @Test
    void getById() throws Exception {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(booking1DtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header(HEADER, user2Dto.getId()))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(booking1DtoResponse)));
    }

    @Test
    void getByBooker() throws Exception {
        when(bookingService.getByBooker(anyLong(), any(String.class), anyInt(), anyInt()))
                .thenReturn(List.of(booking1DtoResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header(HEADER, user2Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(booking1DtoResponse)));
    }

    @Test
    void getByOwner() throws Exception {
        when(bookingService.getByBooker(anyLong(), any(String.class), anyInt(), anyInt()))
                .thenReturn(List.of(booking1DtoResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header(HEADER, user2Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(booking1DtoResponse))));
    }
}
