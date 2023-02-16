package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;

public class ItemRequestMapper {

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester) {
        return new ItemRequest(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requester,
                itemRequestDto.getCreated());
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester().getId(),
                itemRequest.getCreated(),
                new ArrayList<>());
    }
}
