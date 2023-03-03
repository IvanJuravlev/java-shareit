package ru.practicum.shareit.booking;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.data.domain.PageRequest;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;

    private final User user =  User.builder()
            .name("name")
            .email("email@email.ru")
            .build();

    private final User user2 = User.builder()
            .name("name2")
            .email("email2@email.ru")
            .build();
    private final Item item = Item.builder()
            .name("name")
            .description("description")
            .available(true)
            .owner(user)
            .itemRequest(null)
            .build();

    private final Booking booking = Booking.builder()
            .start(LocalDateTime.now().minusHours(3))
            .end(LocalDateTime.now().minusHours(1))
            .item(item)
            .booker(user2)
            .status(null)
            .build();

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findAllByBookerIdOrderByStartDescTest() {
        booking.setStatus(BookingStatus.WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(user2.getId(), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }


    @Test
    void findAllByBookerIdAndStatusTest() {
        booking.setStatus(BookingStatus.WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStatus(user2.getId(), BookingStatus.WAITING, pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerIdOrderByStartDescTest() {
        booking.setStatus(BookingStatus.WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findByItemOwnerIdOrderByStartDesc(user.getId(), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerCurrentTest() {
        booking.setStatus(BookingStatus.WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllItemBookingCurrDate(user.getId(), LocalDateTime.now().minusHours(2), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllItemBookingEndIsBeforeTest() {
        booking.setStatus(BookingStatus.WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllItemBookingEndIsBefore(user.getId(), LocalDateTime.now().plusHours(2), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllItemBookingAndStartIsAfterTest() {
        booking.setStatus(BookingStatus.WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllItemBookingAndStartIsAfter(user.getId(), LocalDateTime.now().minusHours(4), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllItemBookingStatusTest() {
        booking.setStatus(BookingStatus.WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllItemBookingStatus(user.getId(), BookingStatus.WAITING, pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findFirstByBookerAndItemIdAndEndBeforeTest() {
        booking.setStatus(BookingStatus.WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);

        Booking res = bookingRepository.findFirstByBookerAndItemIdAndEndBefore(user2, item.getId(),
                LocalDateTime.now().plusHours(1)).orElseThrow();
        assertEquals(booking, res);
    }

}
