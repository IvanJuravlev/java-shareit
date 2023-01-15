package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InMemoryUserStorage implements UserStorage{
    private final Map<Long, User> userMap = new HashMap<>();
    private long idCounter = 1;

    @Override
    public List<User> getAll(){
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User getById(long id){
        if(!userMap.containsKey(id)){
            throw new NotFoundException("Пользователя с id " + id + " несуществует");
        }
        return userMap.get(id);
    }

    @Override
    public User create(User user){
        user.setId(idCounter++);
        userMap.put(user.getId(), user);
        log.info("Пользователь с id {} создан", user.getId());
        return user;
    }

    @Override
    public User update(long id, User user){
        if(!userMap.containsKey(id)) {
            throw new NotFoundException("Пользователя с id " + id + " несуществует");
        }
            userMap.get(id).setName(user.getName());
            userMap.get(id).setEmail(user.getEmail());
            log.info("Информация о пользователе с id {} обновлена", user.getId());
            return user;
    }

    @Override
    public void delete(long id){
        if(!userMap.containsKey(id)){
            throw new NotFoundException("Пользователя с id " + id + " несуществует");
        }
        userMap.remove(id);
    }


}
