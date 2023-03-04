package ru.practicum.shareit.booking;

import static org.mockito.ArgumentMatchers.anyLong;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotSupportedStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.*;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest
@MockitoSettings(strictness = Strictness.LENIENT)
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {

    @Mock
    private UserService userService;

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

    private UserDto userDto;

    private ShortBookingDto shortBookingDto;

    private UserDto userDto1;
    private UserDto userDto2;
    private ItemDto itemDto1;

    @BeforeEach
    void beforeEach() {

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);

        user1 = new User(1L, "User1 name", "user1@mail.com");
        user2 = new User(2L, "User2 name", "user2@mail.com");

        userDto1 = UserMapper.toUserDto(user1);
        userDto2 = UserMapper.toUserDto(user2);

        userService.create(userDto);

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

        shortBookingDto = ShortBookingDto.builder()
                .id(1L)
                .itemId(item1.getId())
                .start(start)
                .end(end)
                .build();
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingDto bookingDtoResponse = bookingService.create(
                userDto2.getId(),
                shortBookingDto);

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
    }


    @Test
    void createBookingWithBookerAsOwnerUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toShortBookingDto(booking1)
                ));

        assertEquals("Вещь не может быть заказана владельцем", exception.getMessage());


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

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toShortBookingDto(booking1)
                ));

        assertEquals("Вещь не может быть заказана владельцем", exception.getMessage());

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

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toShortBookingDto(booking1)
                ));

        assertEquals("Вещь не может быть заказана владельцем", exception.getMessage());


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

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toShortBookingDto(booking1)));

        assertEquals("Предмет 1 не найден", exception.getMessage());
    }

    @Test
    void createBookingWithWrongUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toShortBookingDto(booking1)));

        assertEquals("Пользователя 1 не существует", exception.getMessage());
    }

    @Test
    void changeStatusTest() {
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

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.approveBookingRequest(
                        user1.getId(),
                        booking1.getId(),
                        true));

        assertEquals("Статус бронирование уже подтверждён", exception.getMessage());
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
    void getBookingInfoBookingNotFoundTest() {
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
    void getBookingInfoYouNotABookerTest() {
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
    void shouldReturnAllByBookerIdWithStateAll() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        bookingService.findByBooker(user1.getId(),
                "ALL",
                0,
                10);

        verify(bookingRepository)
                .findAllByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldReturnAllByBookerIdWithStateCurrent() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        bookingService.findByBooker(user1.getId(),
                "CURRENT",
                0,
                10);

        verify(bookingRepository)
                .findByBookerIdCurrDate(anyLong(), any(), any(Pageable.class));
    }

    @Test
    void shouldReturnAllByBookerIdWithStatePast() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        bookingService.findByBooker(user1.getId(),
                "PAST",
                0,
                10);


        verify(bookingRepository)
                .findAllByBookerIdAndEndIsBefore(anyLong(), any(), any(Pageable.class));
    }

    @Test
    void shouldReturnAllByBookerIdWithStateFuture() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        bookingService.findByBooker(user1.getId(),
                "FUTURE",
                0,
                10);

        verify(bookingRepository)
                .findAllByBookerIdAndStartIsAfter(anyLong(), any(), any(Pageable.class));
    }

    @Test
    void shouldReturnAllByBookerIdWithStateWaiting() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        Pageable pageRequest = PageRequest.of(0, 10);

        bookingService.findByBooker(user1.getId(),
                "WAITING",
                0,
                10);

        verify(bookingRepository)
                .findAllByBookerIdAndStatus(user1.getId(), BookingStatus.WAITING, pageRequest);
    }

    @Test
    void shouldReturnAllByBookerIdWithStateReject() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        Pageable pageRequest = PageRequest.of(0, 10);

        bookingService.findByBooker(user1.getId(),
                "REJECTED",
                0,
                10);

        verify(bookingRepository)
                .findAllByBookerIdAndStatus(user1.getId(), BookingStatus.REJECTED, pageRequest);
    }

    @Test
    void shouldReturnAllByBookerIdWithStateIncorrect() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        Pageable pageRequest = PageRequest.of(0, 10);

        final NotSupportedStateException exception = assertThrows(NotSupportedStateException.class,
                () -> bookingService.findByBooker(user1.getId(), "INCORRECT STATE", 0, 10));

        assertEquals("Incorrect state", exception.getMessage());
    }

    @Test
    void shouldReturnAllByOwnerIdWithStateAll() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        bookingService.findItemBooking(user1.getId(),
                "ALL",
                    0,
                    10);

        verify(bookingRepository)
                .findByItemOwnerIdOrderByStartDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldReturnAllByOwnerIdWithStateCurrent() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        bookingService.findItemBooking(user1.getId(),
                "CURRENT",
                0,
                10);

        verify(bookingRepository)
                .findAllItemBookingCurrDate(anyLong(), any(), any(Pageable.class));
    }

    @Test
    void shouldReturnAllByOwnerIdWithStatePast() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        bookingService.findItemBooking(user1.getId(),
                "PAST",
                0,
                10);

        verify(bookingRepository)
                .findAllItemBookingEndIsBefore(anyLong(), any(), any(Pageable.class));
    }

    @Test
    void shouldReturnAllByOwnerIdWithStateFuture() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        bookingService.findItemBooking(user1.getId(),
                "FUTURE",
                0,
                10);

        verify(bookingRepository)
                .findAllItemBookingAndStartIsAfter(anyLong(), any(), any(Pageable.class));
    }

    @Test
    void shouldReturnAllByOwnerIdWithStateWaiting() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        Pageable pageRequest = PageRequest.of(0, 10);

        bookingService.findItemBooking(user1.getId(),
                "WAITING",
                0,
                10);

        verify(bookingRepository)
                .findAllItemBookingStatus(user1.getId(), BookingStatus.WAITING, pageRequest);
    }

    @Test
    void shouldReturnAllByOwnerIdWithStateReject() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        Pageable pageRequest = PageRequest.of(0, 10);

        bookingService.findItemBooking(user1.getId(),
                "REJECTED",
                0,
                10);

        verify(bookingRepository)
                .findAllItemBookingStatus(user1.getId(), BookingStatus.REJECTED, pageRequest);
    }

    @Test
    void shouldReturnAllByOwnerIdWithStateIncorrect() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        final NotSupportedStateException exception = assertThrows(NotSupportedStateException.class,
                () -> bookingService.findByBooker(user1.getId(), "INCORRECT STATE", 0, 10));

        assertEquals("Incorrect state", exception.getMessage());
    }


    @Test
    void getBookingsWithWrongUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        booking1.setBooker(user2);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findByBooker(
                        22L,
                        "WAITING",
                        0,
                        10));
        assertEquals("Пользователя 16 не существует", exception.getMessage());
    }


    @Test
    void getItemsOwnerWithWrongUserTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findByBooker(user1.getId(),
                        "WAITING",
                        0,
                        10));

        assertEquals("Пользователя 1 не существует", exception.getMessage());
    }
}
