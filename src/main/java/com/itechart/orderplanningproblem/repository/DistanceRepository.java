package com.itechart.orderplanningproblem.repository;

import com.itechart.orderplanningproblem.entity.Distance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DistanceRepository extends JpaRepository<Distance, Long> {

    @Query("select d from Distance d where d.customer.id = :customerId and d.warehouse.id = :warehouseId")
    Distance findDistanceByCustomerAndWarehouse(@Param("customerId") final Long customerId,
                                                @Param("warehouseId") final Long warehouseId);

    @Modifying
    @Query("delete from Distance d where d.warehouse.id = :warehouseId")
    void deleteByWarehouseId(@Param("warehouseId") Long warehouseId);
    @Modifying
    @Query("delete from Distance d where d.customer.id = :customerId")
    void deleteByCustomerId(@Param("customerId") Long customerId);
    @Modifying
    @Query("delete from Distance d where d.warehouse.identifier = :warehouseIdentifier")
    void deleteByWarehouseIdentifier(@Param("warehouseIdentifier") String identifier);

}
