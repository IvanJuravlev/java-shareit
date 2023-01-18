package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

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
        for(User user : userStorage.getAll()){
            if(user.getId() == userId){
                throw new NotFoundException("Пользователь " + userId + " не найден");
            }

        }
        return itemStorage.create(userId, item);
    }

    @Override
    public Item update(long itemId, Item item) {
        return itemStorage.update(itemId, item);
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
