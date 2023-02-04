package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment.Comment;
import ru.practicum.shareit.item.Comment.CommentDto;
import ru.practicum.shareit.item.Comment.CommentMapper;
import ru.practicum.shareit.item.Comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto create(long userId, ItemDto itemDto) {
        User owner = UserMapper.toUser(userService.getById(userId));
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, null, owner));
        return ItemMapper.toItemDto(item);
    }

    public ItemDto getByItemId(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id " + itemId + " не найден"));
        return ItemMapper.toItemDto(item);
    }

    public List<Item> getAll(){
        return itemRepository.findAll();
    }

    @Transactional
    public ItemDto update(long userId, long itemId, ItemUpdateDto itemUpdateDto){
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id " + itemId + " не найден"));

        if (item.getOwner() != userId) {
            throw new NotFoundException("Изменять ифнормацию о предмете может только его владелец");
        }
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
        log.info("Предмет с id {} обновлен", itemId);
        return ItemMapper.toItemDto(item);
    }

    public List<Item> getAllByOwner(long ownerId) {
        userService.getById(ownerId);
        List<Item> items = itemRepository.getAllByOwnerIdOrderByIdAsc(ownerId);
        return items;
    }

    public List<Item> search(String text) {
        List<Item> items = new ArrayList<>();

        if(text.isBlank() || text.isEmpty()){
            return items;
        }

        text.toLowerCase();

        items = itemRepository.search(text);
        return items;
    }

    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto){
        User author = UserMapper.toUser(userService.getById(userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id " + itemId + " не найден"));

        bookingRepository.findFirstByBookerAndItemIdAndEndBefore(author, itemId, LocalDateTime.now()).orElseThrow(() ->
               new NotFoundException("Предмет не был забронирован"));

        Comment comment = CommentMapper.toComment(commentDto, author, item);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    public void delete(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id " + itemId + " не найден"));

        itemRepository.delete(item);
        log.info("Предмет с id {} удален", itemId);
    }
















}
