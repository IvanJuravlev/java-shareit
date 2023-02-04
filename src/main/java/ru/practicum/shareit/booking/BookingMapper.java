package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {
    public Booking toBooking(BookingDto bookingDto, Item item, User user){
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                bookingDto.getStatus());
    }

    public BookingDto bookingDto(Booking booking){
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus());
    }

    public ShortBookingDto shortBookingDto(Booking booking){
        return new ShortBookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd());
    }
}
