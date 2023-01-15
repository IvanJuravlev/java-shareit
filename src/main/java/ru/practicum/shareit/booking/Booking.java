package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

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
