package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Component
public class BookingMapper {

    public  static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus());
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        return new BookingItemDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus());
    }

    public static Booking shortBookingDtoToBooking(ShortBookingDto shortBookingDto, Item item, User user) {
        return new Booking(shortBookingDto.getId(),
                shortBookingDto.getStart(),
                shortBookingDto.getEnd(),
                item,
                user,
                null);

        }

    public static ShortBookingDto toShortBookingDto(Booking booking) {
        return new ShortBookingDto(booking.getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd());
    }
}
