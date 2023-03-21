package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
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

    public UserDto update(long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя с id %x несуществует", id)));

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        log.info("Информация о пользователе с id {} обновлена", id);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public UserDto delete(long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя с id %x несуществует", id)));
        userRepository.deleteById(id);
        return UserMapper.toUserDto(user);
    }
}

