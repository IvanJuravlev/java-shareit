package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
//@AllArgsConstructor
public class PostItemRequestDto {
    @NotBlank
    private String description;
}
