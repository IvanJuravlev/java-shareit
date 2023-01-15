package ru.practicum.shareit.user.storage;



import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();
    User getById(long id);
    User create(User user);
    User update(long id, User user);
    void delete(long id);

}
