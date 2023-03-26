package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingItemDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
    Long bookerId;
    BookingStatus status;
}
