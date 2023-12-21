package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner(User owner, Sort sort);
    List<Item> findItemsByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :userId " +
            "AND b.end >= :currentDate")
    boolean hasValidBookingForItemAndUser(@Param("itemId") Long itemId, @Param("userId") Long userId, @Param("currentDate") LocalDateTime currentDate);

}