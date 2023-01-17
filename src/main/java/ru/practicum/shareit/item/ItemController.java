package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto){
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") long itemId, @RequestBody Item item){  //Тут может быть ошибка
        return itemService.update(itemId, item);
    }

    @GetMapping
    public List<Item> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId){
        return itemService.getAllByOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable long itemId){
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam String text){
        return itemService.searchItem(text);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id){
        itemService.delete(id);
    }

}
