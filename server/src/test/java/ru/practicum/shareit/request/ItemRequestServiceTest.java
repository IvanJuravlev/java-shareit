package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import static org.mockito.Mockito.verify;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    public static final long FAKE_ID = 99999L;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestService service;
    @Spy
    private ItemRequestMapper mapper;
    @Spy
    private ItemMapper itemMapper;
    private User user;
    private Item item;
    private ItemRequest request;
    private PostItemRequestDto postItemRequestDto;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@yandex.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .itemRequest(request)
                .build();

        request = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .created(LocalDateTime.now())
                .requester(user)
                .build();

        postItemRequestDto = PostItemRequestDto.builder()
                .description("Test Description")
                .build();
    }

    @Test
    void addRequest() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(request);

        ItemRequestDto requestDTO = service.create(user.getId(), postItemRequestDto);

        assertEquals(requestDTO.getId(), request.getId());
    }

    @Test
    void addRequestWithWrongUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                service.create(
                        user.getId(),
                        postItemRequestDto
                ));

        assertEquals("Пользователь c id 1 не найден", exception.getMessage());
    }

    @Test
    void addRequestAndCheckRepositoryMethodCalls() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(request);

        service.create(user.getId(), postItemRequestDto);

        verify(userRepository, times(1))
                .findById(user.getId());
        verify(requestRepository, times(1))
                .save(any(ItemRequest.class));
    }


    @Test
    void getRequestListByOwnerId() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(user.getId()))
                .thenReturn(List.of(request));

        List<ItemRequestDto> requestDTOS = service.getOnwRequests(user.getId());

        assertEquals(requestDTOS.get(0).getId(), request.getId());
    }

    @Test
    void getRequestListAndCheckRepositoryMethodCalls() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(user.getId()))
                .thenReturn(List.of(request));

        service.getOnwRequests(user.getId());

        verify(userRepository, times(1))
                .findById(user.getId());
        verify(requestRepository, times(1))
                .findAllByRequesterIdOrderByCreatedDesc(user.getId());
    }

    @Test
    void getRequestListByIncorrectUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                service.getOnwRequests(
                        user.getId()
                ));

        assertEquals("Пользователь c id 1 не найден", exception.getMessage());
    }

    @Test
    void getAllRequestList() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(List.of(request));

        List<ItemRequestDto> requestDTOS = service.getOtherRequests(user.getId(), 0, 10);

        assertEquals(requestDTOS.get(0).getId(), request.getId());
    }

    @Test
    void getAllRequestListAndCheckRepositoryMethodCalls() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(List.of(request));

        service.getOtherRequests(user.getId(), 0, 10);

        verify(userRepository, times(1))
                .findById(user.getId());
        verify(requestRepository, times(1))
                .findAllByRequesterIdIsNotOrderByCreatedDesc(anyLong(), any());
    }

    @Test
    void getAllRequestListByIncorrectUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                service.getOtherRequests(
                        user.getId(), 0, 10
                ));

        assertEquals("Пользователь c id 1 не найден", exception.getMessage());
    }

    @Test
    void getRequestById() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));

        ItemRequestDto requestDTO = service.getById(user.getId(), request.getId());

        assertEquals(requestDTO.getId(), request.getId());
    }

    @Test
    void getRequestByIdAndCheckRepositoryMethodCalls() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));

        service.getById(user.getId(), request.getId());

        verify(userRepository, times(1))
                .findById(user.getId());
        verify(requestRepository, times(1))
                .findById(request.getId());
    }

    @Test
    void getRequestByIncorrectUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                service.getById(
                        user.getId(), request.getId()
                ));

        assertEquals("Пользователь c id 1 не найден", exception.getMessage());
    }

    @Test
    void getRequestByIncorrectRequestId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getById(user.getId(), request.getId()));

        assertEquals("Запрос с id 1 не существует", exception.getMessage());
    }

}
