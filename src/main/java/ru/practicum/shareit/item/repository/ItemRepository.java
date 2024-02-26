package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner(User owner, Pageable pageable);

    @Query("select i " +
            "from Item i " +
            "where i.owner.id = :ownerId")
    List<Item> findByOwnerIdWithoutPageable(Long ownerId);

    List<Item> findAllByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);

    List<Item> findAllByRequestInOrderById(List<ItemRequest> requests);

}