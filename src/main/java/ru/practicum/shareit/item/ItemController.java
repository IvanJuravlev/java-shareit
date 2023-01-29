package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

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
    public Item update(@RequestHeader(header) long userId, @PathVariable long itemId, @RequestBody Item item) {
        return itemService.update(itemId, userId, item);
    }

    @GetMapping
    public List<Item> getAllByOwner(@RequestHeader(header) long ownerId) {
        return itemService.getAllByOwner(ownerId);
    }

    @GetMapping(itemPath)
    public Item getItemById(@PathVariable long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        itemService.delete(id);
    }

}
