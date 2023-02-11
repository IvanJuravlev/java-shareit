package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(HEADER) Long userId,
                             @Valid @RequestBody ShortBookingDto shortBookingDto) {
        return bookingService.create(userId, shortBookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(HEADER) Long userId, @PathVariable Long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findUserBooking(@RequestHeader(HEADER) Long userId,
                                            @RequestParam(defaultValue = "ALL", name = "state") String stateParam) {
        return bookingService.findByBooker(userId, stateParam);
    }

    @GetMapping("/owner")
    public List<BookingDto> findItemBooking(@RequestHeader(HEADER) Long userId,
                                            @RequestParam(defaultValue = "ALL", name = "state") String stateParam) {
        return bookingService.findItemBooking(userId, stateParam);
    }

    @PatchMapping("/{bookingId}")
    public  BookingDto approveRequest(@RequestHeader(HEADER) Long userId, @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {
        return bookingService.approveBookingRequest(userId, bookingId, approved);
    }
}
