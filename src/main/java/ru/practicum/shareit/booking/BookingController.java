package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

//import static ru.practicum.shareit.ShareItApp.HEADER;


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
                                            @RequestParam(defaultValue = "ALL", name = "state") String stateParam,
                                            @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                            @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        return bookingService.getByBooker(userId, stateParam, from, size);
    }

  //  @GetMapping
//    public List<BookingDtoResponse> getByBooker(@RequestHeader(HEADER) long userId,
//                                                @RequestParam(defaultValue = "ALL", required = false) String state,
//                                                @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
//                                                @Positive @RequestParam(defaultValue = "20", required = false) int size) {
//        return bookingService.getByBooker(userId, state, from, size);
//    }

    @GetMapping("/owner")
    public List<BookingDto> findItemBooking(@RequestHeader(HEADER) Long userId,
                                            @RequestParam(defaultValue = "ALL", name = "state") String stateParam,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                            @Positive @RequestParam(defaultValue = "20") int size){
        return bookingService.findItemBooking(userId, stateParam, from, size);
    }

    @PatchMapping("/{bookingId}")
    public  BookingDto approveRequest(@RequestHeader(HEADER) Long userId, @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {
        return bookingService.approveBookingRequest(userId, bookingId, approved);
    }
}
