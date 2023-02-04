package ru.practicum.shareit.item.Comment;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class CommentMapper {

    public static Comment toComment(CommentDto commentDto, User author, Item item){
        return new Comment(commentDto.getId(),
                commentDto.getText(),
                item,
                author,
                commentDto.getCreated());
    }

    public static CommentDto toCommentDto(Comment comment){
        return new CommentDto(comment.getId(),
                comment.getAuthor().getName(),
                comment.getText(),
                comment.getCreated());
    }
}
