package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
     List<Item> getAllByOwner(long ownerId);
     Item getItemById(long id);
     ItemDto create(long userId, ItemDto item);
     Item update(long itemId, Item item);
     void delete(long id);
     List<Item> searchItem(String text);
}
