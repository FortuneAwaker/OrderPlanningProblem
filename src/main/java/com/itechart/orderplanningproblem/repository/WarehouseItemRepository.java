package com.itechart.orderplanningproblem.repository;

import com.itechart.orderplanningproblem.entity.WarehouseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseItemRepository extends JpaRepository<WarehouseItem, Long> {
}
