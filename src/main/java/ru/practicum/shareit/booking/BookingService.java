package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotSupportedStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingDto create(long bookerId, ShortBookingDto shortBookingDto) {
        User booker = UserMapper.toUser(userService.getById(bookerId));
        Item item = itemRepository.findById(shortBookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Вещь не найдена"));

        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException("Вещь не может быть заказана владельцем");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь не доступна для бронирования");
        }
        if (shortBookingDto.getEnd().isBefore(shortBookingDto.getStart())){
            throw new BadRequestException("Не корректное время заказа вещи");
        }

        Booking booking = bookingMapper.shortBookingDtoToBooking(shortBookingDto, item, booker);
        booking.setStatus(WAITING);
        bookingRepository.save(booking);
        log.info("Бронизование создано");
        return bookingMapper.toBookingDto(booking);
    }

    public BookingDto findById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                "Бронирование с id " + bookingId + " не найдено!"));

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if(!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new NotFoundException("У пользователя " + userId + " нет доступа к бронированию " + bookingId);
        }
        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> findByBooker(Long userId, String state) {
        BookingState bookingState = stateToEnum(state);
        userService.getById(userId);
        List<Booking> userBookings;

        switch (bookingState) {
            case ALL:
                userBookings = bookingRepository.findAllByBookerId(userId);
                break;
            case PAST:
                userBookings = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                userBookings = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now());
                break;
            case CURRENT:
                userBookings = bookingRepository.findByBookerIdCurrDate(userId,  LocalDateTime.now());
                break;
            case WAITING:
                userBookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                userBookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                userBookings = new ArrayList<>();
        }

        userBookings.sort(Comparator.comparing(Booking::getStart).reversed());

        return userBookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
     }

    public List<BookingDto> findItemBooking(Long userId, String stateParam) {
        BookingState state = stateToEnum(stateParam);
        userService.getById(userId);
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllItemBooking(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllItemBookingEndIsBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllItemBookingAndStartIsAfter(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllItemBookingCurrDate(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllItemBookingStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllItemBookingStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = new ArrayList<>();
        }

        bookings.sort(Comparator.comparing(Booking::getStart).reversed());

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public BookingDto approveBookingRequest(Long userId, Long bookingId, Boolean approved) {
            Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                    new NotFoundException("Бронирование не найдено"));

            Item item = booking.getItem();
            if (userId != item.getOwner().getId()) {
                throw new NotFoundException("Вы не можете подтвердить бронирование вещи");
            }
            if (booking.getStatus() == BookingStatus.APPROVED) {
                throw new BadRequestException("Статус бронирование уже подтверждён");
            }
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            }
            else booking.setStatus(BookingStatus.REJECTED);

            return BookingMapper.toBookingDto(booking);
     }




    public BookingState stateToEnum(String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            String message = "Incorrect state";
            log.warn(message);
            throw new NotSupportedStateException(message);
        }
        return bookingState;

    }
}
