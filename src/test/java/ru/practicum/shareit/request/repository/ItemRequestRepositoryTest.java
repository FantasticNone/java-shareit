package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        entityManager.persist(user);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Test Description 1");
        request1.setRequester(user);
        entityManager.persist(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Test Description 2");
        request2.setRequester(user);
        entityManager.persist(request2);
    }

    @Test
    void testFindAllByRequesterOrderByCreatedDesc() {
        User user = new User();
        user.setId(1L);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterOrderByCreatedDesc(user);
        assertEquals(2, requests.size());
    }

    @Test
    void testFindAllByRequesterNotOrderByCreatedDesc() {
        User user = new User();
        user.setId(1L);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterNotOrderByCreatedDesc(user, pageRequest);
        assertEquals(2, requests.size());
    }
}
