package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    public ItemRequestDto create(Long userId, PostItemRequestDto postItemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь c id %x не найден", userId)));
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper
                .mapToItemRequest(user, postItemRequestDto, LocalDateTime.now()));
        log.info("Запрос создан с id {}", itemRequest.getId());
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    public ItemRequestDto getById(long requestId, long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь c id %x не найден", userId)));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с id %x не существует", requestId)));
        List<ItemDto> items = itemRepository.findByItemRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        ItemRequestDto itemRequestDtoResponse = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDtoResponse.setItems(items);
        return itemRequestDtoResponse;
    }

    public List<ItemRequestDto> getOtherRequests(long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь c id %x не найден", userId)));
        Pageable pageable = PageRequest.of(from, size);
        List<ItemRequestDto> responseList = itemRequestRepository
                .findAllByRequesterIdIsNotOrderByCreatedDesc(userId, pageable).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        return setItemsToRequests(responseList);
    }

    public List<ItemRequestDto> getOnwRequests(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь c id %x не найден", userId)));
        List<ItemRequestDto> responseList = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        return setItemsToRequests(responseList);
    }


    private List<ItemRequestDto> setItemsToRequests(List<ItemRequestDto> itemRequestDtoResponseList) {
        Map<Long, ItemRequestDto> requests = itemRequestDtoResponseList.stream()
                .collect(Collectors.toMap(ItemRequestDto::getId, film -> film, (a, b) -> b));
        List<Long> ids = requests.values().stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());
        List<ItemDto> items = itemRepository.searchByRequestsId(ids).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        items.forEach(itemDto -> requests.get(itemDto.getRequestId()).getItems().add(itemDto));
        return new ArrayList<>(requests.values());
    }













}
