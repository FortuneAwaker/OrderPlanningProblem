package com.itechart.orderplanningproblem.repository;

import com.itechart.orderplanningproblem.entity.Item;
import com.itechart.orderplanningproblem.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    @Query("select w from Warehouse w where w.identifier = :identifier")
    Optional<Warehouse> readByIdentifier(@Param("identifier") final String identifier);

    void deleteByIdentifier(final String identifier);

}
