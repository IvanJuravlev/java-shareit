package ru.practicum.shareit.item.Comment;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Comment.Comment;
import ru.practicum.shareit.item.Comment.CommentDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;
@Component
public class CommentMapper {

    public Comment toComment(CommentDto commentDto, User author, Item item){
        return new Comment(commentDto.getId(),
                commentDto.getText(),
                item,
                author,
                commentDto.getCreated());
    }

    public CommentDto toCommentDto(Comment comment){
        return new CommentDto(comment.getId(),
                comment.getAuthor().getName(),
                comment.getText(),
                comment.getCreated());
    }

//    @Override
//    public List<CommentDTO> toDTOList(List<Comment> comments) {
//        return comments.stream()
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }

}
