package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ItemRequestMapper {

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester) {
        return new ItemRequest(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requester,
                itemRequestDto.getCreated());
    }

    public List<ItemRequestDto> mapToItemRequestDto(Iterable<ItemRequest> itemRequests) {
        List<ItemRequestDto> result = new ArrayList<>();

        for (var el : itemRequests) {
            result.add(toItemRequestDto(el));
        }

        return result;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester().getId(),
                itemRequest.getCreated(),
                new ArrayList<>());
    }

    public static ItemRequest mapToItemRequest(User requester, PostItemRequestDto postItemRequestDto,
                                               LocalDateTime date) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(postItemRequestDto.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(date);
        return  itemRequest;
    }
}
