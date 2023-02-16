package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;

    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User requester = UserMapper.toUser(userService.getById(userId));
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.toItemRequest(itemRequestDto, requester));
        log.info("Запрос создан с id {}", itemRequest.getId());
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    public ItemRequestDto getById(long requestId, long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с id %x не существует", requestId)));
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    public List<ItemRequestDto> getOwnRequests(long userId, int from, int size) {
        userService.getById(userId);
        Pageable pageable = PageRequest.of(from, size, Sort.by("created"));
        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository.findAllByRequesterIdNot(userId, pageable).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        return itemRequestDtoList;
    }


}
