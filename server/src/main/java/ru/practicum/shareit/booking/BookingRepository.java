package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime date, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime date, Pageable pageable);

    List<Booking> findByItemIdAndBookerIdAndEndLessThanAndStatus(
            Long id, Long id1, LocalDateTime end, BookingStatus status);

    Optional<Booking> findFirstByBookerAndItemIdAndEndBefore(User booker, Long itemId, LocalDateTime date);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId, Pageable pageable);

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.start < :date and :date < b.end and " +
            "b.booker.id = :bookerId")
    List<Booking> findByBookerIdCurrDate(Long bookerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId and " +
            "b.end < :date")
    List<Booking> findAllItemBookingEndIsBefore(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId and " +
            "b.start > :date")
    List<Booking> findAllItemBookingAndStartIsAfter(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.start < :date and :date < b.end and " +
            "b.item.owner.id = :ownerId")
    List<Booking> findAllItemBookingCurrDate(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId and b.status = :status")
    List<Booking> findAllItemBookingStatus(Long ownerId, BookingStatus status, Pageable pageable);

    @Query("select distinct booking from Booking booking " +
            "where booking.end < ?2 " +
            "and booking.item.id = ?1 " +
            "order by booking.start desc ")
    Optional<Booking> findLastBooking(long itemId, LocalDateTime now);

    @Query("select distinct booking from Booking booking " +
            "where booking.start > ?2 " +
            "and booking.item.id = ?1 " +
            "order by booking.start ")
    Optional<Booking> findNextBooking(long itemId, LocalDateTime now);

    @Query("select b from Booking b where b.item.id in ?1 and b.item.owner.id = ?2 and b.status != 'REJECTED'" +
            " and b.start < ?3 order by b.start desc")
    List<Booking> findByItemIdAndOwnerIdAndStartDateLessThenNowInOrderByIdDesc(
            Collection<Long> ids, Long ownerId, LocalDateTime time);

    @Query("select b from Booking b where b.item.id in ?1 and b.item.owner.id = ?2 and b.status != 'REJECTED'" +
            " and b.start > ?3 order by b.start")
    List<Booking> findByItemIdAndOwnerIdAndStartDateIsMoreThenNowInOrderByIdAsc(
            Collection<Long> ids, Long ownerId, LocalDateTime time);
}
