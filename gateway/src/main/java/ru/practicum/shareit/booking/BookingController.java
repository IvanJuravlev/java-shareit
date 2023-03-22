package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import ru.practicum.shareit.booking.dto.BookingDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.NotSupportedStateException;

@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {

    private final BookingClient bookingClient;

    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER) long id, @Valid @RequestBody BookingDto bookingDto) {
        return bookingClient.create(id, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBookingRequest(@RequestHeader(HEADER) long userId,
                                               @PathVariable long bookingId,
                                               @RequestParam boolean approved) {
        return bookingClient.approveBookingRequest(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader(HEADER) long userId,
                                          @PathVariable long bookingId) {
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findByBooker(@RequestHeader(HEADER) long userId,
                                              @RequestParam(defaultValue = "ALL", required = false) String state,
                                              @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                              @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        BookingState status = BookingState.from(state).orElseThrow(()
                -> new NotSupportedStateException(String.format("Unknown state: %s", state)));
        return bookingClient.findByBooker(userId, String.valueOf(status), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findItemBooking(@RequestHeader(HEADER) long userId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state,
                                             @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                             @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        return bookingClient.findItemBooking(userId, state, from, size);
    }
}