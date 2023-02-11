package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment.Comment;
import ru.practicum.shareit.item.Comment.CommentDto;
import ru.practicum.shareit.item.Comment.CommentMapper;
import ru.practicum.shareit.item.Comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    public ItemDto create(long userId, ItemDto itemDto) {
        User owner = UserMapper.toUser(userService.getById(userId));
        Item item = itemRepository.save(itemMapper.toItem(itemDto, owner));
        itemDto.setId(item.getId());
        return itemMapper.toItemDto(item);
    }

    public ItemBookingDto getByItemId(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id " + itemId + " не найден"));

        ItemBookingDto newItemDto = setComments(setBookings(userId, item), itemId);
        log.warn("Резульатат " + newItemDto.getComments());
        return newItemDto;
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    @Transactional
    public ItemDto update(long userId, long itemId, ItemUpdateDto itemUpdateDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id " + itemId + " не найден"));

        if (item.getOwner().getId() == userId) {
            if (itemUpdateDto.getName() != null) {
                item.setName(itemUpdateDto.getName());
            }
            if (itemUpdateDto.getDescription() != null) {
                item.setDescription(itemUpdateDto.getDescription());
            }
            if (itemUpdateDto.getAvailable() != null) {
                item.setAvailable(itemUpdateDto.getAvailable());
            }
            itemRepository.save(item);
            log.info("Item updated");
        } else {
            throw new NotFoundException("Вещь для обновления не найдена");
        }

        return itemMapper.toItemDto(item);
    }

    public List<ItemBookingDto> getAllByOwner(long ownerId) {
        userService.getById(ownerId);

        return itemRepository.getAllByOwnerIdOrderByIdAsc(ownerId).stream()
                .map(item -> setBookings(ownerId, item))
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        List<Item> items = new ArrayList<>();
        if (text.isBlank() || text.isEmpty()) {
            return items;
        }
        text.toLowerCase();
        items = itemRepository.search(text);
        return items;
    }

    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = UserMapper.toUser(userService.getById(userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id " + itemId + " не найден"));

        bookingRepository.findFirstByBookerAndItemIdAndEndBefore(author, itemId, LocalDateTime.now()).orElseThrow(() ->
               new BadRequestException("Предмет не был забронирован"));
        Comment comment = commentMapper.toComment(commentDto, author, item);
        comment.setCreated(LocalDateTime.now());

        commentRepository.save(comment);
        log.warn("комментарий" + comment);
        return commentMapper.toCommentDto(comment);
    }

    public void delete(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id " + itemId + " не найден"));

        itemRepository.delete(item);
        log.info("Предмет с id {} удален", itemId);
    }


    private ItemBookingDto setBookings(long userId, Item item) {
        ItemBookingDto itemDtoBooking = itemMapper.toItemBookingDto(item);
        if (item.getOwner().getId() == userId) {
            itemDtoBooking.setLastBooking(
                    bookingRepository.findLastBooking(
                            itemDtoBooking.getId(), LocalDateTime.now()
                    ).map(BookingMapper::toBookingItemDto).orElse(null));
            itemDtoBooking.setNextBooking(
                    bookingRepository.findNextBooking(
                            itemDtoBooking.getId(), LocalDateTime.now()
                    ).map(BookingMapper::toBookingItemDto).orElse(null));
        } else {
            itemDtoBooking.setLastBooking(null);
            itemDtoBooking.setNextBooking(null);
        }
        return itemDtoBooking;
    }

    private ItemBookingDto setComments(ItemBookingDto itemBookingDto, long itemId) {
        List<CommentDto> commentDtos = commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemBookingDto.setComments(commentDtos);
        return itemBookingDto;
    }

















}
