package ru.practicum.shareit.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import static org.mockito.Mockito.times;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.when;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.verify;
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
    void getByItemIdTest() {
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
    void create() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(repository.save(any(Item.class)))
                .thenReturn(item1);

        ItemDto itemDto = itemService.create(item1.getId(), ItemMapper.toItemDto(item1));

        assertEquals(1, itemDto.getId());
        assertEquals("Item1 name", itemDto.getName());
        assertEquals("Item1 description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }


    @Test
    void createInappropriateItemWithNoUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.create(
                        user1.getId(),
                        ItemMapper.toItemDto(item1)
                ));

        assertEquals("Пользователя id 1 не существует", exception.getMessage());
    }

    @Test
    void getItemsByUserIdWithoutBookings() {
        when(repository.getAllByOwnerIdOrderByIdAsc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(item1));

        List<ItemBookingDto> items = itemService.getAllByOwner(user1.getId(), 0, 10);

        assertEquals(items.get(0).getId(), item1.getId());
        assertEquals(items.get(0).getName(), item1.getName());
        assertNull(items.get(0).getLastBooking());
        assertNull(items.get(0).getNextBooking());
    }

    @Test
    void getItemByIdWithIncorrectUserId() {
        when(userRepository.findById(55L))
                .thenThrow(new NotFoundException("User not found"));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.delete(55L, item1.getId()));

        assertEquals("Пользователя id 1 не существует", exception.getMessage());
    }

    @Test
    void removeItemById() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        itemService.delete(item1.getId(), user1.getId());

        verify(userRepository, times(1)).findById(user1.getId());
        verify(repository, times(1)).deleteById(item1.getId());
    }

    @Test
    void  createInappropriateItemWithNoRequestIdTest() {
        item1.setItemRequest(null);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.create(
                        user1.getId(),
                        ItemMapper.toItemDto(item1)
                ));

        assertEquals("Пользователя id 1 не существует", exception.getMessage());
    }

    @Test
    void creatItemWithNullItemRequestTest() {
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
    void updateTest() {
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
    void searchItemWithNameInUpperFirstLetterTest() {

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
    void searchItemWithNameInRandomUpperCaseTest() {
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
    void searchItemWithDescriptionInRandomUpperCaseTest() {
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
    void searchItemWithDescriptionInUpperFirstLetterTest() {
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
    void addComment() {
        when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(
                anyLong(),
                anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(Optional.ofNullable(booking1));

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment1);

        CommentDto commentDto = itemService
                .addComment(1L, 1L, CommentMapper.toCommentDto(comment1));

        assertEquals(1, commentDto.getId());
        assertEquals("Comment1 text", commentDto.getText());
        assertEquals("User1 name", commentDto.getAuthorName());
    }

    @Test
    void createCommentTest() {
        when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(
                anyLong(),
                anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking1));

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment1);

        CommentDto commentDto = itemService.addComment(
                1,
                1,
                CommentMapper.toCommentDto(comment1)
        );

        assertEquals(1, commentDto.getId());
        assertEquals("Comment1 text", commentDto.getText());
        assertEquals("User1 name", commentDto.getAuthorName());
    }


    @Test
    void createCommentFromUserWithoutBookingTest() {
        when(bookingRepository.findFirstByBookerAndItemIdAndEndBefore(
                any(),
                anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(
                        1L,
                        1L,
                        CommentMapper.toCommentDto(comment1)
                ));

        assertEquals(
                "User not found",
                exception.getMessage());
    }

    @Test
    void updateItemWithIncorrectUserId() {
        User anotherUser = User.builder()
                .id(55L)
                .build();

        ItemDto itemForUpdate = ItemDto.builder()
                .name("New Name")
                .build();

        item1.setOwner(user1);

        when(userRepository.findById(anotherUser.getId()))
                .thenReturn(Optional.of(anotherUser));
        when(repository.findById(item1.getId()))
                .thenReturn(Optional.of(item1));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(55L, item1.getId(), itemForUpdate));

        assertEquals("Вещь для обновления не найдена", exception.getMessage());
    }

    @Test
    void updateItemAvailableStatus() {
        item1.setOwner(user1);

        ItemDto itemForUpdate = ItemDto.builder()
                .available(false)
                .build();

        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        when(repository.findById(item1.getId()))
                .thenReturn(Optional.of(item1));
        when(repository.save(any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto updatedItem = itemService.update(user1.getId(), item1.getId(), itemForUpdate);

        assertEquals(item1.getId(), updatedItem.getId());
        assertFalse(updatedItem.getAvailable());
    }


    @Test
    void updateItemDescription() {
        item1.setOwner(user1);

        ItemDto itemForUpdate = ItemDto.builder()
                .description("New Description")
                .build();

        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        when(repository.findById(item1.getId()))
                .thenReturn(Optional.of(item1));
        when(repository.save(any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto updatedItem = itemService.update(user1.getId(), item1.getId(), itemForUpdate);

        assertEquals(item1.getId(), updatedItem.getId());
        assertEquals("New Description", updatedItem.getDescription());
    }

    @Test
    void updateItemName() {
        item1.setOwner(user1);

        ItemDto itemForUpdate = ItemDto.builder()
                .name("New Name")
                .build();

        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        when(repository.findById(item1.getId()))
                .thenReturn(Optional.of(item1));
        when(repository.save(any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto updatedItem = itemService.update(user1.getId(), item1.getId(), itemForUpdate);

        assertEquals(item1.getId(), updatedItem.getId());
        assertEquals("New Name", updatedItem.getName());
    }
}
