package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String header = "X-Sharer-User-Id";
    private final String itemPath = "/{itemId}";

    @PostMapping
    public ItemDto create(@RequestHeader (header) long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping(itemPath)
    public ItemDto update(@RequestHeader(header) long userId, @PathVariable long itemId, @RequestBody ItemUpdateDto item) {
        return itemService.update(userId, itemId, item);
    }

    @GetMapping
    public List<ItemBookingDto> getAllByOwner(@RequestHeader(header) long ownerId) {
        return itemService.getAllByOwner(ownerId);
    }

    @GetMapping(itemPath)
    public ItemBookingDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return itemService.getByItemId(userId, itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam String text) {
        return itemService.search(text);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        itemService.delete(id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

}
