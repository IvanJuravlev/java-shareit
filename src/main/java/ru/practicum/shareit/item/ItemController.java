package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER = "X-Sharer-User-Id";
    private final String itemPath = "/{itemId}";

    @PostMapping
    public ItemDto create(@RequestHeader (HEADER) long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping(itemPath)
    public ItemDto update(@RequestHeader(HEADER) long userId, @PathVariable long itemId, @RequestBody ItemUpdateDto item) {
        return itemService.update(userId, itemId, item);
    }

    @GetMapping
    public List<ItemBookingDto> getAllByOwner(@RequestHeader(HEADER) long ownerId,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                              @Positive @RequestParam(defaultValue = "10") int size) {
        return itemService.getAllByOwner(ownerId, from, size);
    }

    @GetMapping(itemPath)
    public ItemBookingDto getItemById(@RequestHeader(HEADER) Long userId, @PathVariable Long itemId) {
        return itemService.getByItemId(userId, itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam String text,
                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                 @Positive @RequestParam(defaultValue = "10") int size) {
        return itemService.search(text, from, size);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        itemService.delete(id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HEADER) Long userId, @PathVariable Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

}
