package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final String idPath = "/{id}";

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping(idPath)
    public UserDto getById(@PathVariable long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping(idPath)
    public UserDto update(@PathVariable long id, @RequestBody UserDto userDto) {
        return userService.update(id, userDto);
    }

    @DeleteMapping(idPath)
    public UserDto delete(@PathVariable long id) {
       return userService.delete(id);
    }

}
