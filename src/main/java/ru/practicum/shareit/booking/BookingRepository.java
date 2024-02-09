package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    boolean existsByItemAndBookerAndEndIsBefore(Item item, User user, LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id IN :itemsIds")
    List<Booking> findAllByOwnerItems(List<Long> itemsIds, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.id " +
            "IN :itemsIds " +
            "AND b.status = :waiting")
    List<Booking> findAllByOwnerItemsAndWaitingStatus(List<Long> itemsIds, BookingStatus waiting, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.id " +
            "IN :itemsIds " +
            "AND b.status " +
            "IN :rejected")
    List<Booking> findAllByOwnerItemsAndRejectedStatus(List<Long> itemsIds, List<BookingStatus> rejected, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.id " +
            "IN :itemsIds " +
            "AND b.start < :now " +
            "AND b.end > :now")
    List<Booking> findAllByOwnerItemsAndCurrentStatus(List<Long> itemsIds, LocalDateTime now, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.id " +
            "IN :itemsIds " +
            "AND b.start > :now")
    List<Booking> findAllByOwnerItemsAndFutureStatus(List<Long> itemsIds, LocalDateTime now, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.id " +
            "IN :itemsIds " +
            "AND b.end < :now")
    List<Booking> findAllByOwnerItemsAndPastStatus(List<Long> itemsIds, LocalDateTime now, Sort sort);

    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId " +
            "AND b.status = :waiting")
    List<Booking> findAllByBookerIdAndWaitingStatus(Long bookerId, BookingStatus waiting, Sort sort);

    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId " +
            "AND b.status " +
            "IN :rejected")
    List<Booking> findAllByBookerIdAndRejectedStatus(Long bookerId, List<BookingStatus> rejected, Sort sort);

    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId " +
            "AND b.start < :now " +
            "AND b.end > :now ")
    List<Booking> findAllByBookerIdAndCurrentStatus(Long bookerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId " +
            "AND b.start > :now ")
    List<Booking> findAllByBookerIdAndFutureStatus(Long bookerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId " +
            "AND b.end < :now")
    List<Booking> findAllByBookerIdAndPastStatus(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItem_IdIn(List<Long> itemIds, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.id IN :itemIds " +
            "and b.status = 'APPROVED'")
    List<Booking> findApprovedByItems(List<Long> itemIds);
}
