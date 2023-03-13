package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.ItemDto;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER) long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(HEADER) long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(HEADER) long id,
                                          @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                          @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        return itemClient.getAllByOwner(id, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(HEADER) long userId,
                                             @RequestParam String text,
                                             @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                             @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        return itemClient.searchItem(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HEADER) long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@RequestHeader(HEADER) long userId,
                       @PathVariable long itemId) {
       return itemClient.delete(itemId, userId);
    }
}