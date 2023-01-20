package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final ItemStorage itemStorage;

    @Override
    public List<Item> getAllByOwner(long ownerId) {
        return itemStorage.getAllByOwner(ownerId);
    }

    @Override
    public Item getItemById(long id) {
       return itemStorage.getItemById(id);
    }

    @Override
    public ItemDto create(long userId, ItemDto item) {
        return itemStorage.create(userId, item);
    }

    @Override
    public Item update(long itemId, long userId, Item item) {
        return itemStorage.update(itemId, userId, item);
    }

    @Override
    public void delete(long id) {
        itemStorage.delete(id);
    }

    @Override
    public List<Item> searchItem(String text) {
        return itemStorage.searchItem(text);
    }
}
