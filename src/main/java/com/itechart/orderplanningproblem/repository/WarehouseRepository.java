package com.itechart.orderplanningproblem.repository;

import com.itechart.orderplanningproblem.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {


}
