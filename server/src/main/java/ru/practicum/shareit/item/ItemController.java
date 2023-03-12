package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER = "X-Sharer-User-Id";
    private final String itemPath = "/{itemId}";

    @PostMapping
    public ItemDto create(@RequestHeader (HEADER) long userId, @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping(itemPath)
    public ItemDto update(@RequestHeader(HEADER) long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemBookingDto> getAllByOwner(@RequestHeader(HEADER) long ownerId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        return itemService.getAllByOwner(ownerId, from, size);
    }

    @GetMapping(itemPath)
    public ItemBookingDto getItemById(@RequestHeader(HEADER) Long userId, @PathVariable Long itemId) {
        return itemService.getByItemId(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                 @RequestParam(defaultValue = "0") int from,
                                 @RequestParam(defaultValue = "10") int size) {
        return itemService.search(text, from, size);
    }

    @DeleteMapping("{id}")
    public void delete(@RequestHeader(HEADER) long userId,
                       @PathVariable long itemId) {
        itemService.delete(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HEADER) long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

}
