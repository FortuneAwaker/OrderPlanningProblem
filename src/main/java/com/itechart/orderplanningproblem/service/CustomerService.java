package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.CustomerDtoWithId;
import com.itechart.orderplanningproblem.dto.CustomerDtoWithoutId;
import com.itechart.orderplanningproblem.entity.Customer;
import com.itechart.orderplanningproblem.entity.Distance;
import com.itechart.orderplanningproblem.entity.Warehouse;
import com.itechart.orderplanningproblem.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.repository.CustomerRepository;
import com.itechart.orderplanningproblem.repository.DistanceRepository;
import com.itechart.orderplanningproblem.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final DistanceRepository distanceRepository;
    private final WarehouseRepository warehouseRepository;
    private final ObjectMapper objectMapper;
    private final DistanceService distanceService;

    private static final String CUSTOMER_NAME_SHOULD_BE_UNIQUE_LITERAL = "Customer with such name already exists. " +
            "Customer name should be unique!";

    @Transactional
    public CustomerDtoWithId create(final CustomerDtoWithoutId customerDtoWithoutId) throws UnprocessableEntityException {
        Optional<Customer> fromDbByName = customerRepository.readByName(customerDtoWithoutId.getName());
        if (fromDbByName.isPresent()) {
            throw new UnprocessableEntityException(CUSTOMER_NAME_SHOULD_BE_UNIQUE_LITERAL);
        }
        Customer customerFromDto = objectMapper.convertValue(customerDtoWithoutId, Customer.class);
        Customer createdCustomer = customerRepository.save(customerFromDto);
        mapCustomerToExistentWarehouses(createdCustomer);
        return objectMapper.convertValue(createdCustomer, CustomerDtoWithId.class);
    }

    private void mapCustomerToExistentWarehouses(final Customer customer) {
        List<Warehouse> allWarehouses = warehouseRepository.findAll();
        List<Distance> distances = new ArrayList<>();
        for (Warehouse warehouse: allWarehouses) {
            double distanceValue = distanceService.getDistanceByLatitudeAndLongitude(
                    customer.getLatitude(), customer.getLongitude(),
                    warehouse.getLatitude(), warehouse.getLongitude());
            Distance distance = new Distance(null, distanceValue, customer, warehouse);
            distances.add(distance);
        }
        distanceRepository.saveAll(distances);
    }

    public CustomerDtoWithId readById(final Long id) throws ResourceNotFoundException {
        return customerRepository.findById(id).map(
                customer -> objectMapper.convertValue(customer, CustomerDtoWithId.class))
                .orElseThrow(() -> new ResourceNotFoundException("Item with id = " + id + " doesn't exist"));
    }

    public Page<CustomerDtoWithId> readPage(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customer -> objectMapper.convertValue(customer, CustomerDtoWithId.class));
    }

    @Transactional
    public void deleteById(final Long id) {
        if (customerRepository.findById(id).isEmpty()) {
            return;
        }
        customerRepository.deleteById(id);
    }
}
