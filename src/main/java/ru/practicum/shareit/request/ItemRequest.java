package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    long id;
    String description;
    long requester;
    LocalDate created;
}
