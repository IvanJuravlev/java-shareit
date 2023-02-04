package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.Comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

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


    public List<Item> getAllByOwner(long ownerId) {

        return itemStorage.getAllByOwner(ownerId);
    }


    public Item getItemById(long id) {
       return itemStorage.getItemById(id);
    }





    public Item update(long itemId, long userId, Item item) {
        return itemStorage.update(itemId, userId, item);
    }


    public void delete(long id) {
        itemStorage.delete(id);
    }


    public List<Item> searchItem(String text) {
        return itemStorage.searchItem(text);
    }
}
