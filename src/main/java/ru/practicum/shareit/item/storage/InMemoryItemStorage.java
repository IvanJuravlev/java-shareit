package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ChangeException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private long idCounter = 1;
    private final UserStorage userStorage;
  //  private final ItemMapper itemMapper;


    @Override
    public List<Item> getAllByOwner(long ownerId){
        List<Item> itemsByOwner = new ArrayList<>();

        for (Item item : itemMap.values()){
            if(item.getOwner() == ownerId){
                itemsByOwner.add(item);
            }
        }
        return itemsByOwner;
    }

    @Override
    public Item getItemById(long id){
        if(!itemMap.containsKey(id)){
            throw new NotFoundException("Пользователя с id " + id + " несуществует");
        }
        return itemMap.get(id);
    }

    @Override
    public ItemDto create(long userId, ItemDto item) {
        item.setId(idCounter++);
        for (User user : userStorage.getAll()) {
            if (user.getId() == userId) {
                throw new NotFoundException("Пользователь " + userId + " не найден");
            }
        }
        itemMap.put(item.getId(), ItemMapper.toItem(item, userId));
        log.info("Предмет с id {} создан", item.getId());
        return item;
    }

    @Override
    public Item update(long itemId, Item item){
        if (!itemMap.containsKey(itemId)){
            throw new NotFoundException("Предмета с id " + itemId + " несуществует");
        }
        Item prevItem = itemMap.get(itemId);

        if (item.getOwner() != prevItem.getOwner()) {
            throw new ChangeException("Изменение предмета доступно только владельцу");
        }
        if (item.getName() != null) {
            prevItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            prevItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            prevItem.setAvailable(item.getAvailable());
        }
        log.info("Предмет с id {} обновлен", prevItem.getId());
        return prevItem;
    }

    @Override
    public void delete(long id){
        if(!itemMap.containsKey(id)){
            throw new NotFoundException("Предмета с id " + id + " несуществует");
        }
        itemMap.remove(id);
        log.info("Предмет с id {} удален", id);
    }

    @Override
    public List<Item> searchItem(String text){
        List<Item> itemsList = new ArrayList<>();
        if (text.isBlank() || text.isEmpty()) {
            return itemsList;
        }

        text = text.toLowerCase();
        for (Item item : itemMap.values()){
            if(!item.getAvailable()){
                continue;
            }
            if (item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text)) {
                itemsList.add(item);
            }
        }
        return itemsList;
    }

}
