package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserSrvice;

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
    private final UserSrvice userService;


    @Override
    public List<Item> getAll(){
        return new ArrayList<>(itemMap.values());
    }

    @Override
    public Item getItemById(long id){
        if(!itemMap.containsKey(id)){
            throw new NotFoundException("Пользователя с id " + id + " несуществует");
        }
        return itemMap.get(id);
    }

    @Override
    public Item create(Item item){
        item.setId(idCounter++);
        userService.getById(item.getOwner());
        itemMap.put(item.getId(), item);
        log.info("Предмет с id={} создан", item.getId());
        return item;
    }

}
