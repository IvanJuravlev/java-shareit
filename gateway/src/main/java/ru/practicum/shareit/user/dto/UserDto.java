package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {
    long id;
    @NotBlank(message = "Name is required")
    String name;
    @Email(message = "Email is incorrect")
    @NotBlank(message = "Email is required")
    String email;
}
