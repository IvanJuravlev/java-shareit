package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Comment.CommentDto;

import java.time.LocalDateTime;
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
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;
    //Возможно поменять тут!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
}
