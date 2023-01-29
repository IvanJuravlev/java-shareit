package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    long id;
    LocalDate start;
    LocalDate end;
    long item;
    long booker;
    BookingStatus status;

}
