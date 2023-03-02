package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemRequestServiceTest {

    @InjectMocks
    private ItemRequestService itemRequestService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    private LocalDateTime now = LocalDateTime.now();

    private User user = new User(
            1L,
            "name",
            "email@email.ru");

    private ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "description",
            1L,
            LocalDateTime.now(),
            null);

    private PostItemRequestDto postItemRequestDto = new PostItemRequestDto(
            "description"
    );

    private ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);


    private Item item = new Item(
            1L,
            "name",
            "description",
            true,
            user,
            null);


    @Test
    void create() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        ItemRequest itemRequest1 = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        when(requestRepository.save(any())).thenReturn(itemRequest1);

        ItemRequestDto newDto = itemRequestService.create(user.getId(), postItemRequestDto);
        itemRequestDto.setCreated(newDto.getCreated());

        assertEquals(itemRequestDto.getId(), newDto.getId());
        verify(requestRepository, Mockito.times(1)).save(any());

    }

    @Test
    void createWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("User not found"));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.create(1L, postItemRequestDto));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getOwnRequestsWhenUserFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        List<ItemRequestDto> responseList = itemRequestService.getOnwRequests(user.getId());
        assertTrue(responseList.isEmpty());
        verify(requestRepository).findAllByRequesterIdOrderByCreatedDesc(anyLong());
    }


    @Test
    void getOtherRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyLong())).thenThrow(new NotFoundException("Запрос не найден"));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemRequestService.getOtherRequests(user.getId(), 1, 2)
        );

        assertEquals("Запрос не найден", exception.getMessage());
    }

    @Test
    void getOtherRequestsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(requestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getOtherRequests(
                user.getId(),
                0,
                10);

        assertEquals(1, itemRequestDtos.size());
        assertEquals(1, itemRequestDtos.get(0).getId());
        assertEquals("description", itemRequestDtos.get(0).getDescription());
        assertEquals(user.getId(), itemRequestDtos.get(0).getRequesterId());
        assertEquals(Collections.emptyList(), itemRequestDtos.get(0).getItems());
    }

}
