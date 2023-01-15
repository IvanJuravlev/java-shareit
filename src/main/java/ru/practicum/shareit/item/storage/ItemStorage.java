package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
     List<Item> getAll();
     Item getItemById(long id);
     Item create(Item item);
     Item update(Item item);
     void delete(long id);
    List<ItemDto> searchItem(String text);
}
