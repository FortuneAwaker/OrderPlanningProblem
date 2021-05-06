package com.itechart.orderplanningproblem.repository;

import com.itechart.orderplanningproblem.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where i.name = :name")
    Optional<Item> readByName(@Param("name") final String name);

    void deleteByName(final String name);

}
