package ru.practicum.shareit.item;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.ArrayList;
import java.util.Objects;

@Component
public class ItemMapper {

    public static Item toItem(ItemDto itemDto, User user,ItemRequest itemRequest) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest);
    }

    public static ItemDto toItemDto(Item item) {
        Long requestId = null;
        if (item.getItemRequest() != null) {
            requestId = item.getItemRequest().getId();
        }
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                requestId);
    }

    public static ItemResponseDto toResponseDto(Item item, UserDto userDTO) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(userDTO)
                .comments(new ArrayList<>())
                .requestId(Objects.isNull(item.getItemRequest()) ? null : item.getItemRequest().getId())
                .build();
    }

    public static ItemBookingDto toItemBookingDto(Item item) {
        return new ItemBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>());
    }
}
