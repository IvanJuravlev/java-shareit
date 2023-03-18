package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment.Comment;
import ru.practicum.shareit.item.Comment.CommentDto;
import ru.practicum.shareit.item.Comment.CommentMapper;
import ru.practicum.shareit.item.Comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.*;

import java.time.LocalDateTime;
import java.util.*;
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
        User owner = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Пользователя id %x не существует", userId));
        });
        ItemRequest itemRequest = null;
        Long itemRequestId = itemDto.getRequestId();
        if (itemRequestId != null) {
            itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(() ->
                    new NotFoundException(String.format("Запроса с id %x не существует", itemRequestId)));
        }
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, owner, itemRequest));
        itemDto.setId(item.getId());
        return itemMapper.toItemDto(item);
    }

    public ItemResponseDto getByItemId(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Предмет id %x не найден", itemId)));

        Map<Long, List<Booking>> lastBookings = getAllLastBookingsByItemId(Set.of(itemId), userId);
        Map<Long, List<Booking>> nextBookings = getAllNextBookingsByItemId(Set.of(itemId), userId);

        UserDto ownerDTO = UserMapper.toUserDto(item.getOwner());
        ItemResponseDto itemResponseDto = ItemMapper.toResponseDto(item, ownerDTO);
        setBookingsToDTO(
                Optional.ofNullable(lastBookings.get(item.getId())),
                Optional.ofNullable(nextBookings.get(item.getId())),
                itemResponseDto
        );
        setCommentsToDTO(itemResponseDto);

        return itemResponseDto;
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

    public List<ItemResponseDto> getAllByOwner(long ownerId, int from, int size) {
        User user = UserMapper.toUser(userService.getById(ownerId));
        Pageable pageable = PageRequest.of(from, size);
        List<Item> items = itemRepository.getAllByOwnerIdOrderByIdAsc(ownerId, pageable);

        Set<Long> ids = items.stream().map(Item::getId).collect(Collectors.toSet());
        Map<Long, List<Booking>> lastBookings = getAllLastBookingsByItemId(ids, user.getId());
        Map<Long, List<Booking>> nextBookings = getAllNextBookingsByItemId(ids, user.getId());

        return items.stream().map(item -> {
            ItemResponseDto itemResponseDto = ItemMapper.toResponseDto(item, UserMapper.toUserDto(user));
            setBookingsToDTO(
                    Optional.ofNullable(lastBookings.get(item.getId())),
                    Optional.ofNullable(nextBookings.get(item.getId())),
                    itemResponseDto
            );
            setCommentsToDTO(itemResponseDto);

            return itemResponseDto;
        }).collect(Collectors.toList());

//        List<ItemBookingDto> itemBookingDtoList = itemRepository.getAllByOwnerIdOrderByIdAsc(ownerId, pageable).stream()
//                .map(item -> setBookings(ownerId, item))
//                .collect(Collectors.toList());
//        return itemBookingDtoList;
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


    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndEndLessThanAndStatus(
                itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED);
        if(bookings.isEmpty()) {
            throw new BadRequestException("Вы не можете комментировать эту вещь");
        }
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = CommentMapper.toComment(commentDto, user, item);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    public void delete(long itemId, long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Пользователя id %x не существует", userId));
        });
        itemRepository.deleteById(itemId);
        log.info("Предмет с id {} удален", itemId);
    }

    private void setBookingsToDTO(
            Optional<List<Booking>> lastBookings, Optional<List<Booking>> nextBookings, ItemResponseDto itemResponseDto
    ) {

        itemResponseDto
                .setLastBooking(lastBookings.flatMap(bookings -> Optional.ofNullable(bookings.get(0))
                                .map(value -> BookingMapper.toItemResponseDto(value, UserMapper.toUserDto(value.getBooker()))))
                        .orElse(null));

        itemResponseDto
                .setNextBooking(nextBookings.flatMap(bookings -> Optional.ofNullable(bookings.get(0))
                                .map(value -> BookingMapper.toItemResponseDto(value, UserMapper.toUserDto(value.getBooker()))))
                        .orElse(null));
    }

    private Map<Long, List<Booking>> getAllLastBookingsByItemId(Set<Long> itemIds, Long userId) {
        return bookingRepository.findByItemIdAndOwnerIdAndStartDateLessThenNowInOrderByIdDesc(
                        itemIds, userId, LocalDateTime.now()).stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
    }

    private Map<Long, List<Booking>> getAllNextBookingsByItemId(Set<Long> itemIds, Long userId) {
        return bookingRepository.findByItemIdAndOwnerIdAndStartDateIsMoreThenNowInOrderByIdAsc(
                        itemIds, userId, LocalDateTime.now())
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
    }

    private void setCommentsToDTO(ItemResponseDto itemResponseDto) {
        List<Comment> comments = commentRepository.findAllByItemId(itemResponseDto.getId());

        itemResponseDto.setComments(comments.stream()
                .map(comment -> CommentMapper.toCommentDto(comment))//возможно тут добаить User DTO
                .collect(Collectors.toList()));
    }


//    private ItemBookingDto setBookings(long userId, Item item) {
//        ItemBookingDto itemDtoBooking = itemMapper.toItemBookingDto(item);
//        if (item.getOwner().getId() == userId) {
//            itemDtoBooking.setLastBooking(
//                    bookingRepository.findLastBooking(
//                            itemDtoBooking.getId(), LocalDateTime.now()
//                    ).map(BookingMapper::toBookingItemDto).orElse(null));
//            itemDtoBooking.setNextBooking(
//                    bookingRepository.findNextBooking(
//                            itemDtoBooking.getId(), LocalDateTime.now()
//                    ).map(BookingMapper::toBookingItemDto).orElse(null));
//        } else {
//            itemDtoBooking.setLastBooking(null);
//            itemDtoBooking.setNextBooking(null);
//        }
//        return itemDtoBooking;
//    }

//    private ItemBookingDto setComments(ItemBookingDto itemBookingDto, long itemId) {
//        List<CommentDto> commentDtos = commentRepository.findAllByItemId(itemId).stream()
//                .map(CommentMapper::toCommentDto)
//                .collect(Collectors.toList());
//        itemBookingDto.setComments(commentDtos);
//        return itemBookingDto;
//    }

}
