package ru.practicum.shareit.Item.comment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.Comment.Comment;
import ru.practicum.shareit.item.Comment.CommentRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item1;

    private Comment comment1;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        User user1 = userRepository.save(new User(1L, "User1 name", "user1@mail.com"));
        User user2 = userRepository.save(new User(2L, "User2 name", "user2@mail.com"));

        item1 = Item.builder()
                .id(1L)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .itemRequest(null)
                .build();
        item1 = itemRepository.save(item1);

        comment1 = Comment.builder()
                .id(1L)
                .text("Comment1 text")
                .item(item1)
                .author(user2)
                .created(now)
                .build();
        comment1 = commentRepository.save(comment1);
    }

    @AfterEach
    void afterEach() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllComments() {
        List<Long> ids = itemRepository.findAll().stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findAllByItemId(1L);

        assertEquals(List.of(comment1).size(), comments.size());
        assertEquals(comment1.getId(), comments.get(0).getId());
        assertEquals(comment1.getAuthor(), comments.get(0).getAuthor());
        assertEquals(comment1.getText(), comments.get(0).getText());
    }
}