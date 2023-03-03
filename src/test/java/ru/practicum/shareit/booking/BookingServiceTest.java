package ru.practicum.shareit.booking;

import static org.mockito.ArgumentMatchers.anyLong;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import java.time.LocalDateTime;
import java.util.Optional;
import org.mockito.Mock;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotSupportedStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private LocalDateTime start;

    private LocalDateTime end;

    private User user1;

    private User user2;

    private Item item1;

    private Booking booking1;

    @BeforeEach
    void beforeEach() {

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);

        user1 = new User(1L, "User1 name", "user1@mail.com");
        user2 = new User(2L, "User2 name", "user2@mail.com");

        item1 = Item.builder()
                .id(1L)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .itemRequest(null)
                .build();

        booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
    }


    @Test
    void createBookingWithBookerAsOwnerUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

         assertThrows(NullPointerException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toShortBookingDto(booking1)
                ));


    }

    @Test
    void createBookingOnNotAvailableItemTest() {
        item1.setAvailable(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

         assertThrows(NullPointerException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toShortBookingDto(booking1)));

    }

    @Test
    void createBookingWithWrongTimeTest() {
        booking1.setStart(LocalDateTime.now().minusDays(3));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        assertThrows(NullPointerException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toShortBookingDto(booking1)));


    }

    @Test
    void createBookingOnNotExistingItemTest() {
        booking1.setStart(LocalDateTime.now().minusDays(3));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        assertThrows(NullPointerException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toShortBookingDto(booking1)));


    }

    @Test
    void createBookingWithWrongUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        assertThrows(NullPointerException.class,
                () -> bookingService.create(1L,
                        BookingMapper.toShortBookingDto(booking1)));

    }

    @Test
    void changeStatus() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingDto bookingDtoResponse = bookingService.approveBookingRequest(
                user1.getId(),
                booking1.getId(),
                true);

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
        assertEquals(BookingStatus.APPROVED, bookingDtoResponse.getStatus());
    }

    @Test
    void updateBookingWithWrongIdTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approveBookingRequest(
                        user2.getId(),
                        booking1.getId(),
                        true));

        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void updateBookingFromWrongUserTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approveBookingRequest(
                        user2.getId(),
                        booking1.getId(),
                        true));

        assertEquals("Вы не можете подтвердить бронирование вещи", exception.getMessage());
    }

    @Test
    void changeStatusBookingStatusApprovedTwiceTest() {

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        booking1.setStatus(BookingStatus.APPROVED);

        assertThrows(BadRequestException.class,
                () -> bookingService.approveBookingRequest(
                        user1.getId(),
                        booking1.getId(),
                        true));
    }

    @Test
    void updateBookingRejectTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingDto bookingDtoResponse = bookingService.approveBookingRequest(
                user1.getId(),
                booking1.getId(),
                false);

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
        assertEquals(BookingStatus.REJECTED, bookingDtoResponse.getStatus());
    }

    @Test
    void getBookingTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        BookingDto bookingDtoResponse = bookingService.findById(
                booking1.getId(),
                user1.getId());

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponse.getStatus());
    }

    @Test
    void getBookingInfoBookingNotFound() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findById(
                        user1.getId(),
                        booking1.getId()));

        assertEquals("Бронирование с 1 не найдено", exception.getMessage());
    }

    @Test
    void getBookingInfoYouNotABooker() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        booking1.setBooker(user2);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findById(
                        5L,
                        booking1.getId()));

        assertEquals("У пользователя 5 нет доступа к бронированию 1", exception.getMessage());
    }


    @Test
    void getBookingsUnknownStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        assertThrows(NotSupportedStateException.class,
                () -> bookingService.findByBooker(user1.getId(),
                        "UNKNOWN",
                        0,
                        10));


    }

    @Test
    void getBookingsWithWrongUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        booking1.setBooker(user2);

        assertThrows(NullPointerException.class,
                () -> bookingService.findByBooker(
                        5L,
                        "WAITING",
                        0,
                        10));
    }




    @Test
    void getItemsOwnerBookingsUnknownStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        NotSupportedStateException exception = assertThrows(NotSupportedStateException.class,
                () -> bookingService.findByBooker(user1.getId(),
                        "UNKNOWN",
                        0,
                        10));

        assertEquals("Incorrect state", exception.getMessage());
    }

    @Test
    void getItemsOwnerWithWrongUser() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> bookingService.findByBooker(user1.getId(),
                        "WAITING",
                        0,
                        10));
    }
}
