package ru.practicum.shareit.Item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;

    private Item item1;


    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        user1 = userRepository.save(new User(1L, "User1 name", "user1@mail.com"));

        ItemRequest itemRequest1 = itemRequestRepository
                .save(new ItemRequest(1L, "ItemRequest1 description", user1, now));

        item1 = itemRepository
                .save(new Item(1L, "Item1 name", "Item1 description", true, user1, itemRequest1));
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void getAllByOwnerIdOrderByIdAscTest() {
        List<Item> items = itemRepository.getAllByOwnerIdOrderByIdAsc(user1.getId(), PageRequest.of(0, 10));
        List<Item> items1 = new ArrayList<>();
        items1.add(item1);

        assertEquals(items1.get(0).getId(), items.get(0).getId());
        assertEquals(items1.get(0).getName(), items.get(0).getName());
        assertEquals(items1.get(0).getDescription(), items.get(0).getDescription());
    }

    @Test
    void searchByTextTestFindDescriptionTest() {

        String text = "description";
        List<Item> items = itemRepository.search(text, PageRequest.of(0, 10));

        assertEquals(List.of(item1).size(), items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item1.getName(), items.get(0).getName());
    }

    @Test
    void searchByTextTestFindNameTest() {
        String text = "name";

        List<Item> items = itemRepository.search(text, PageRequest.of(0, 10));

        assertEquals(List.of(item1).size(), items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item1.getName(), items.get(0).getName());
    }

    @Test
    void searchByRequestsIdTestTest() {
        List<Long> ids = itemRepository.findAll().stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.searchByRequestsId(ids);

        assertEquals(List.of(item1).size(), items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item1.getName(), items.get(0).getName());
    }
}
