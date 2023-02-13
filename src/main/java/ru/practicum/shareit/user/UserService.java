package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedEmailException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }


    public UserDto getById(long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя с id %x несуществует", id)));
        return UserMapper.toUserDto(user);
    }


    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.info("Пользователь с id {} создан", user.getId());
        return UserMapper.toUserDto(user);

    }

    @Transactional
    public UserDto update(long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя с id %x несуществует", id)));

        if (userDto.getEmail() != null) {
            checkIfEmailExists(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        log.info("Информация о пользователе с id {} обновлена", id);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public void delete(long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя с id %x несуществует", id)));
        userRepository.deleteById(id);
    }

    private void checkIfEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            String message = String.format("Пользователь таким с email %s уже существует", email);
            log.warn(message);
            throw new DuplicatedEmailException(message);
        }
    }
}

