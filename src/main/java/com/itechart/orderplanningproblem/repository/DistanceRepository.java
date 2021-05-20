package com.itechart.orderplanningproblem.repository;

import com.itechart.orderplanningproblem.entity.Distance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface DistanceRepository extends JpaRepository<Distance, Long> {

    Distance findDistanceByCustomerIdAndWarehouseId(final Long customerId, final Long warehouseId);

    @Modifying
    void deleteByWarehouseId(Long warehouseId);
    @Modifying
    void deleteByCustomerId(Long customerId);

}
