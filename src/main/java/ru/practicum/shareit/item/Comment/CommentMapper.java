package ru.practicum.shareit.item.Comment;

import ru.practicum.shareit.item.model.Item;

public class CommentMapper {

    public static Comment toComment(CommentDto commentDto, Item item){
        return new Comment(commentDto.getId(),
                commentDto.getText(),
                item,
                commentDto.getAuthorName(),
                commentDto.getCreated())
    }
}
