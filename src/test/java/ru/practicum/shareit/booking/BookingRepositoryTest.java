package ru.practicum.shareit.booking;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;
;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;

    User user = new User(
            1L,
            "name",
            "email@email.ru");
    User user2 = new User(
            2L,
            "name2",
            "email2@email.ru");
    Item item = new Item(
            1L,
            "name",
            "description",
            true,
            user,
            null);
    Booking booking = new Booking(
            1L,
            LocalDateTime.now().minusHours(3),
            LocalDateTime.now().minusHours(1),
            item,
            user2,
            null);

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findAllByBookerIdOrderByStartDescTest() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(user2.getId(), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));//id могут сбиться, проверить при запуске всех тестов
    }

    @Test
    void findAllItemBookingCurrDateTest() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllItemBookingCurrDate(user2.getId(), LocalDateTime.now().minusHours(2), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllItemBookingEndIsBeforeTest1() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllItemBookingEndIsBefore(user2.getId(), LocalDateTime.now().plusHours(2), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllItemBookingEndIsBeforeTest2() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllItemBookingEndIsBefore(user2.getId(), LocalDateTime.now().minusHours(4), pg);

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
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);

        Booking res = bookingRepository.findFirstByBookerAndItemIdAndEndBefore(user2, item.getId(),
                LocalDateTime.now().plusHours(1)).orElseThrow();
        assertEquals(booking, res);
    }

}
