package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;



    @Transactional
    public ItemDto create(long userId, ItemDto itemDto) {
        User owner = UserMapper.toUser(userService.getById(userId));
        ItemRequest itemRequest = null;
        Long itemRequestId = itemDto.getRequestId();
        if (itemRequestId != null) {
            itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(() ->
                    new NotFoundException(String.format("Запроса с id %x не существует", itemRequestId)));
        }
        Item item = itemRepository.save(itemMapper.toItem(itemDto, owner, itemRequest));
        itemDto.setId(item.getId());
        return itemMapper.toItemDto(item);
    }

    public ItemBookingDto getByItemId(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Предмет id %x не найден", itemId)));

        ItemBookingDto newItemDto = setComments(setBookings(userId, item), itemId);
        return newItemDto;
    }


    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Предмет id %x не найден", itemId)));

        if (item.getOwner().getId() == userId) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            itemRepository.save(item);
            log.info("Item updated");
        } else {
            throw new NotFoundException("Вещь для обновления не найдена");
        }
        return itemMapper.toItemDto(item);
    }

    public List<ItemBookingDto> getAllByOwner(long ownerId, int from, int size) {
        userService.getById(ownerId);
        Pageable pageable = PageRequest.of(from, size);
        List<ItemBookingDto> itemBookingDtoList = itemRepository.getAllByOwnerIdOrderByIdAsc(ownerId, pageable).stream()
                .map(item -> setBookings(ownerId, item))
                .collect(Collectors.toList());
        return itemBookingDtoList;
    }

    public List<ItemDto> search(String text, int from, int size) {
        if (text.isBlank() || text.isEmpty()) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(from, size);
        return itemRepository.search(text.toLowerCase(), pageable)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

//    @Transactional
//    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
//        User author = UserMapper.toUser(userService.getById(userId));
//        Item item = itemRepository.findById(itemId).orElseThrow(() ->
//                new NotFoundException(String.format("Предмет id %x не найден", itemId)));
//
//        bookingRepository.findFirstByBookerAndItemIdAndEndBefore(author, itemId, LocalDateTime.now()).orElseThrow(() ->
//               new BadRequestException("Предмет не был забронирован"));
//        Comment comment = commentMapper.toComment(commentDto, author, item);
//        comment.setCreated(LocalDateTime.now());
//
//        commentRepository.save(comment);
//        log.warn("Добавлен комментарий {} ", comment);
//        return commentMapper.toCommentDto(comment);
//    }


    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Вы не можете комментировать эту вещь"));
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = CommentMapper.toComment(commentDto, user, item);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    public void delete(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Предмет id %x не найден", itemId)));

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
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemBookingDto.setComments(commentDtos);
        return itemBookingDto;
    }

}
