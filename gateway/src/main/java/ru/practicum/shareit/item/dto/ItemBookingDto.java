package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemBookingDto {
    long id;
    String name;
    String description;
    boolean available;
    BookingItemDto lastBooking;
    BookingItemDto nextBooking;
    List<CommentDto> comments;
}