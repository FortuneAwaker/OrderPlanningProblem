package com.itechart.orderplanningproblem.repository;

import com.itechart.orderplanningproblem.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> readByName(final String name);

    @Modifying
    void deleteByName(final String name);

}
