package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User getById(long id);

    UserDto create(UserDto userDto);

    User update(long id, User user);

    void delete(long id);
}
