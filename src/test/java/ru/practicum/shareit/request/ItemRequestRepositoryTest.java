package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;

    private ItemRequest itemRequest1;


    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        user1 = new User(1L, "User1 name", "user1@mail.com");
        user1 = userRepository.save(user1);

        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("ItemRequest1 description")
                .requester(user1)
                .created(now)
                .build();
        itemRequest1 = itemRequestRepository.save(itemRequest1);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findByRequesterId() {
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(user1.getId());
        assertEquals(List.of(itemRequest1), itemRequestList);
    }

}
