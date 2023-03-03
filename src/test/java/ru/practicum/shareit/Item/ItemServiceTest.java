package ru.practicum.shareit.Item;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment.Comment;
import ru.practicum.shareit.item.Comment.CommentDto;
import ru.practicum.shareit.item.Comment.CommentMapper;
import ru.practicum.shareit.item.Comment.CommentRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.when;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import org.mockito.InjectMocks;
import java.util.Collections;
import java.util.Optional;
import org.mockito.Mock;
import ru.practicum.shareit.user.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private User user1;

    private User user2;

    private Item item1;

    private Booking booking1;

    private UserDto userDto1;

    private Comment comment1;

    private LocalDateTime now;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        user1 = new User(1L, "User1 name", "user1@mail.com");
        userRepository.save(user1);
        user2 = new User(2L, "User2 name", "user2@mail.com");
        userRepository.save(user2);
        item1 = new Item(1L, "Item1 name", "Item1 description", true, user1, null);

        userDto1 = new UserDto(101L, "user1", "user1@mail.ru");

        booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();

        comment1 = Comment.builder()
                .id(1L)
                .text("Comment1 text")
                .item(item1)
                .author(user2)
                .created(now)
                .build();
    }


    @Test
    void getByItemId() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        ItemBookingDto itemBookingDto = itemService.getByItemId(
                item1.getId(),
                user1.getId());

        assertEquals(1, itemBookingDto.getId());
        assertEquals("Item1 name", itemBookingDto.getName());
        assertEquals("Item1 description", itemBookingDto.getDescription());
        assertEquals(true, itemBookingDto.isAvailable());
    }


    @Test
    void createInappropriateItemWithNoUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NullPointerException.class, () ->
                itemService.create(
                        user1.getId(),
                        ItemMapper.toItemDto(item1)
                ));
    }

    @Test
    void  createInappropriateItemWithNoRequestId() {
        item1.setItemRequest(null);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NullPointerException.class, () ->
                itemService.create(
                        user1.getId(),
                        ItemMapper.toItemDto(item1)
                ));
    }

    @Test
    void creatItemWithNullItemRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        ItemDto itemDto = ItemMapper.toItemDto(item1);
        itemDto.setRequestId(3000L);

        assertThrows(NullPointerException.class, () ->
                itemService.create(user1.getId(), itemDto)
        );
    }

    @Test
    void update() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(repository.save(any(Item.class)))
                .thenReturn(item1);

        ItemDto itemDto = itemService.update(item1.getId(), user1.getId(), ItemMapper.toItemDto(item1));

        assertEquals(1, itemDto.getId());
        assertEquals("Item1 name", itemDto.getName());
        assertEquals("Item1 description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void updateItemFromNotOwnerTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(repository.save(any(Item.class)))
                .thenReturn(item1);

        assertThrows(NotFoundException.class,
                () -> itemService.update(
                        50L,
                        user2.getId(),
                        ItemMapper.toItemDto(item1)));
    }

    @Test
    void updateItemFromNotUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        user1.setId(1L);
        NotFoundException exc = assertThrows(NotFoundException.class,
                () -> itemService.getByItemId(2L, item1.getId()));

        assertEquals("Предмет id 1 не найден", exc.getMessage());
    }

    @Test
    void updateItemFromNotItemTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        item1.setId(2L);
        user1.setId(1L);
        ItemDto itemDto = ItemMapper.toItemDto(item1);
        NotFoundException exc = assertThrows(NotFoundException.class,
                () -> itemService.update(user1.getId(), 1L, itemDto));

        assertEquals("Предмет id 1 не найден", exc.getMessage());
    }

    @Test
    void searchItemWithNameInUpperFirstLetter() {

        List<ItemDto> itemDtos = itemService.search("Item1", 0, 20);

        assertEquals(Collections.emptyList(), itemDtos);
    }

    @Test
    void searchItemWithBlancText() {

        when(repository.search(anyString(), any(PageRequest.class)))
                .thenReturn(List.of(item1));
        List<ItemDto> itemDtos = itemService.search("", 0, 20);

    }

    @Test
    void searchItemWithNameInRandomUpperCase() {
        when(repository.search(anyString(), any(PageRequest.class)))
                .thenReturn(List.of(item1));

        List<ItemDto> itemDtos = itemService.search("iTem1", 0, 20);

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("Item1 name", itemDtos.get(0).getName());
        assertEquals("Item1 description", itemDtos.get(0).getDescription());
        assertEquals(true, itemDtos.get(0).getAvailable());
        assertNull(itemDtos.get(0).getRequestId());
    }

    @Test
    void searchItemWithDescriptionInRandomUpperCase() {
        when(repository.search(anyString(), any(PageRequest.class)))
                .thenReturn(List.of(item1));

        List<ItemDto> itemDtos = itemService.search("desCription", 0, 20);

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("Item1 name", itemDtos.get(0).getName());
        assertEquals("Item1 description", itemDtos.get(0).getDescription());
        assertEquals(true, itemDtos.get(0).getAvailable());
        assertNull(itemDtos.get(0).getRequestId());
    }

    @Test
    void searchItemWithDescriptionInUpperFirstLetter() {
        when(repository.search(anyString(), any(PageRequest.class)))
                .thenReturn(List.of(item1));

        List<ItemDto> itemDtos = itemService.search("desCription", 0, 20);

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("Item1 name", itemDtos.get(0).getName());
        assertEquals("Item1 description", itemDtos.get(0).getDescription());
        assertEquals(true, itemDtos.get(0).getAvailable());
        assertNull(itemDtos.get(0).getRequestId());
    }


    @Test
    void createCommentFromUserWithoutBookingTest() {
        when(bookingRepository.findFirstByBookerAndItemIdAndEndBefore(
                any(),
                anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> itemService.addComment(
                        1L,
                        1L,
                        CommentMapper.toCommentDto(comment1)
                ));

    }
}
