package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> getAllByOwnerIdOrderByIdAsc(Long ownerId, Pageable pageable);

    @Query("select i from Item i where " +
            "(lower(i.name) like lower(concat('%', :text,'%')) or " +
            "lower(i.description) like lower(concat('%', :text,'%'))) " +
            "and i.available = true")
    List<Item> search(String text, Pageable pageable);

    List<Item> findByItemRequestId(long requestId);

    @Query("select item from Item item " +
            "where item.itemRequest.id in :ids")
    List<Item> searchByRequestsId(@Param("ids") List<Long> ids);
}



