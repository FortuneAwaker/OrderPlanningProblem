package com.itechart.orderplanningproblem.repository;

import com.itechart.orderplanningproblem.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    @Query("select w from Warehouse w where w.identifier = :identifier")
    Optional<Warehouse> readByIdentifier(@Param("identifier") final String identifier);

    @Query("select w from Warehouse w join Distance d on w.id = d.warehouse.id" +
            " where d.customer.id = :customerId order by d.distanceValue asc")
    List<Warehouse> findWarehouseForCustomer(@Param("customerId") final Long customerId);

}
