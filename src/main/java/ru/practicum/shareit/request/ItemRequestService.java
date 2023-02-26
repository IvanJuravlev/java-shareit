package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

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
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;
    private final ItemMapper itemMapper;

    public ItemRequestDto create(Long userId, PostItemRequestDto postItemRequestDto) {
        User requester = UserMapper.toUser(userService.getById(userId));
        ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper
                .mapToItemRequest(requester, postItemRequestDto, LocalDateTime.now()));
        log.info("Запрос создан с id {}", itemRequest.getId());
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    public ItemRequestDto getById(long requestId, long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с id %x не существует", requestId)));
        List<ItemDto> items = itemRepository.findByItemRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        ItemRequestDto itemRequestDtoResponse = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDtoResponse.setItems(items);
        return itemRequestDtoResponse;
//        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
//        setItemsToRequests(itemRequestDto);
//        return itemRequestDto;
    }

    public List<ItemRequestDto> getOtherRequests(long userId, int from, int size) {
        userService.getById(userId);
        Pageable pageable = PageRequest.of(from, size);
        List<ItemRequestDto> responseList = itemRequestRepository
                .findAllByRequesterIdIsNotOrderByCreatedDesc(userId, pageable).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        return setItemsToRequests(responseList);
//        List<ItemRequest> itemRequests = itemRequestRepository
//                .findAllByRequesterIdIsNotOrderByCreatedDesc(userId, pageable);
//        List<ItemRequestDto> dtoItemRequests = itemRequestMapper.mapToItemRequestDto(itemRequests);
//        dtoItemRequests.forEach(this::setItemsToRequests);
//        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository
//                .findAllByRequesterIdIsNotOrderByCreatedDesc(userId, pageable).stream()
//                .map(itemRequestMapper::toItemRequestDto)
//                .collect(Collectors.toList());
      //  return dtoItemRequests;
    }

    public List<ItemRequestDto> getOnwRequests(long userId) {
        userService.getById(userId);
        List<ItemRequestDto> responseList = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        return setItemsToRequests(responseList);
//        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
//        List<ItemRequestDto> dtoItemRequests = itemRequestMapper.mapToItemRequestDto(itemRequests);
//        dtoItemRequests.forEach(this::setItemsToRequests);
////        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository
////                .findAllByRequesterIdOrderByCreatedDesc(userId)
////                .stream()
////                .map(itemRequestMapper::toItemRequestDto)
////                .collect(Collectors.toList());
//        return dtoItemRequests;
    }


//    private void setItemsToRequests(ItemRequestDto itemRequestDto) {
//        List<Item> itemList = itemRepository.findAllByRequestId(itemRequestDto.getId());
//        itemRequestDto.setItems(itemMapper.toItemDto(itemList));
//    }

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
