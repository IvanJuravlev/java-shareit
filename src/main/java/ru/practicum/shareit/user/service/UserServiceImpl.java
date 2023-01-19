package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserStorage userStorage;

    @Override
    public List<User> getAll(){
        return userStorage.getAll();
    }
    @Override
    public User getById(long id){
        return userStorage.getById(id);
    }
    @Override
    public UserDto create(UserDto user){
        checkIfEmailExists(user.getEmail());
        return userStorage.create(user);

    }
    @Override
    public User update(long id, User user){
        checkIfEmailExists(user.getEmail());
        return userStorage.update(id, user);
    }
    @Override
    public void delete(long id){
        userStorage.delete(id);
    }

    private void checkIfEmailExists(String email) {
        for(User user : userStorage.getAll()){
            if(user.getEmail().equals(email)){
                throw new DuplicatedEmailException("Пользователь таким с email " + email + " уже существует");
            }
        }
    }

}
