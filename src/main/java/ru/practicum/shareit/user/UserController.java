package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
public class UserController {

    @GetMapping
    public List<User> getAllUsers(){
        return ;
    }

    @GetMapping("{/id}")
    public User getUser(@PathVariable int id){
        return ;
    }

    @PostMapping
    public User createUser(@RequestBody User user){
        return ;
    }

}
