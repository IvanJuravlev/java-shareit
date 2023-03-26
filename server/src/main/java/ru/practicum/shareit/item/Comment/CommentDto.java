package ru.practicum.shareit.item.Comment;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    long id;
    String authorName;
    String text;
    LocalDateTime created;
}
