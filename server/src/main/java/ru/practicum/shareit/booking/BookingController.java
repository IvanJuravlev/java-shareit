package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {

    private final BookingService bookingService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(HEADER) Long userId,
                             @RequestBody ShortBookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(HEADER) Long userId, @PathVariable Long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findUserBooking(@RequestHeader(HEADER) Long userId,
                                            @RequestParam(defaultValue = "ALL", name = "state") String stateParam,
                                            @RequestParam(defaultValue = "0", required = false) int from,
                                            @RequestParam(defaultValue = "20", required = false) int size) {
        return bookingService.findByBooker(userId, stateParam, from, size);
    }


    @GetMapping("/owner")
    public List<BookingDto> findItemBooking(@RequestHeader(HEADER) Long userId,
                                            @RequestParam(defaultValue = "ALL", name = "state") String stateParam,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "20") int size) {
        return bookingService.findItemBooking(userId, stateParam, from, size);
    }

    @PatchMapping("/{bookingId}")
    public  BookingDto approveRequest(@RequestHeader(HEADER) Long userId, @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {
        return bookingService.approveBookingRequest(userId, bookingId, approved);
    }
}
