package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;


public class ItemMapper {

    public static Item toItem(ItemDto itemDto, long userId) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId);
    }
}
