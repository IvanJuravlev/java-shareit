package ru.practicum.shareit.booking;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.data.domain.PageRequest;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

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
        booking.setStatus(WAITING);
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
        booking.setStatus(WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStatus(user2.getId(), WAITING, pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByBookerCurrentTest() {
        booking.setStatus(WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findByBookerIdCurrDate(user2.getId(), LocalDateTime.now().minusHours(2), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerIdOrderByStartDescTest() {
        booking.setStatus(WAITING);
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
    void findAllByBookerIdAndEndIsBeforeTest() {
        booking.setStatus(WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndEndIsBefore(user2.getId(), LocalDateTime.now().plusHours(2), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }


    @Test
    void findByBookerFutureTest() {
        booking.setStatus(WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStartIsAfter(user2.getId(), LocalDateTime.now().minusHours(4), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerCurrentTest() {
        booking.setStatus(WAITING);
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
        booking.setStatus(WAITING);
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
        booking.setStatus(WAITING);
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
        booking.setStatus(WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllItemBookingStatus(user.getId(), WAITING, pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }


    @Test
    void findBookingsLastTest() {
        booking.setStatus(WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        Booking booking1 = booking;
        booking1.setEnd(LocalDateTime.now().minusHours(6));
        booking1.setStart(LocalDateTime.now().minusHours(5));
        em.persist(booking1);

        Optional<Booking> res = bookingRepository.findLastBooking(item.getId(), LocalDateTime.now());

        assertTrue(res.isPresent());
        assertEquals(res.get().getStart(), booking.getStart());
        assertEquals(res.get().getEnd(), booking.getEnd());

    }

    @Test
    void findBookingsNextTest() {
        booking.setStatus(WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        booking.setEnd(LocalDateTime.now().plusHours(7));
        booking.setStart(LocalDateTime.now().plusHours(6));
        em.persist(booking);
        Booking booking1 = booking;
        booking1.setEnd(LocalDateTime.now().plusHours(4));
        booking1.setStart(LocalDateTime.now().plusHours(3));
        em.persist(booking1);

        Optional<Booking> res = bookingRepository.findNextBooking(item.getId(), LocalDateTime.now());

        assertTrue(res.isPresent());
        assertEquals(res.get().getStart(), booking.getStart());
        assertEquals(res.get().getEnd(), booking.getEnd());
    }

}
