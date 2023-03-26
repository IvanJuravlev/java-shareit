package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER) long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader(HEADER) long userId) {
        return itemRequestClient.getRequestsInfo(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(HEADER) long userId,
                                                 @PathVariable long requestId) {
        return itemRequestClient.getById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherRequests(@RequestHeader(HEADER) long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                                  @Positive @RequestParam(defaultValue = "10", required = false) int size) {
        return itemRequestClient.getOtherRequests(userId, from, size);
    }
}
