package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.PostItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(HEADER) long userId,
                                        @Valid @RequestBody PostItemRequestDto postItemRequestDto) {
        return itemRequestService.create(userId, postItemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader(HEADER) long userId) {
        return itemRequestService.getOnwRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherRequests(@RequestHeader(HEADER) long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Positive @RequestParam(defaultValue = "10") int size) {
        return itemRequestService.getOtherRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable long requestId, @RequestHeader(HEADER) long userId) {
        return itemRequestService.getById(requestId, userId);
    }


}
