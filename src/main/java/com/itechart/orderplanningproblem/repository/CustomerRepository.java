package com.itechart.orderplanningproblem.repository;

import com.itechart.orderplanningproblem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select c from Customer c where c.name = :name")
    Optional<Customer> readByName(@Param("name") final String name);

}
