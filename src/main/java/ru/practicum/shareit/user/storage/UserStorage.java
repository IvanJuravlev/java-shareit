package ru.practicum.shareit.user.storage;



import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

public interface UserStorage {
    List<User> getAll();
    User getById(long id);
    UserDto create(UserDto user);
    User update(long id, User user);
    void delete(long id);

}
