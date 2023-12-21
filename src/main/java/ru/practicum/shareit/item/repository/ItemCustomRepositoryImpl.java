package ru.practicum.shareit.item.repository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/*@Repository
public class ItemCustomRepositoryImpl implements ItemCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Item> findByCriteria(String pattern) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Item> criteria = cb.createQuery(Item.class);
        Root<Item> item = criteria.from(Item.class);
        criteria.select(item).where(
                cb.and(
                        cb.or(
                                cb.like(cb.upper(item.get("name")), "%" + pattern.toUpperCase() + "%"),
                                cb.like(cb.upper(item.get("description")), "%" + pattern.toUpperCase() + "%")
                        ),
                        cb.equal(item.get("available"), true)
                )
        );
        return entityManager.createQuery(criteria).getResultList();
    }
}*/
