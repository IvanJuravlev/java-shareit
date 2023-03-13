package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ItemRequestDto {
    Long id;
    @NotEmpty
    String description;
    Long requesterId;
    LocalDateTime created;
    List<ItemDto> items;
}
