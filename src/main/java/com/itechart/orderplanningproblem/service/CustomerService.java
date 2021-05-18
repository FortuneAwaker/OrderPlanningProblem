package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.CustomerDto;
import com.itechart.orderplanningproblem.entity.Customer;
import com.itechart.orderplanningproblem.entity.Distance;
import com.itechart.orderplanningproblem.entity.Warehouse;
import com.itechart.orderplanningproblem.error.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.error.exception.UnprocessableEntityException;
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
    public CustomerDto create(final CustomerDto customerDto) {
        checkInDbByName(customerDto.getName());
        Customer customerFromDto = objectMapper.convertValue(customerDto, Customer.class);
        Customer createdCustomer = customerRepository.save(customerFromDto);
        mapCustomerToExistentWarehouses(createdCustomer);
        return objectMapper.convertValue(createdCustomer, CustomerDto.class);
    }

    private void mapCustomerToExistentWarehouses(final Customer customer) {
        List<Warehouse> allWarehouses = warehouseRepository.findAll();
        List<Distance> distances = new ArrayList<>();
        allWarehouses.forEach((warehouse -> {
            double distanceValue = distanceService.getDistanceByLatitudeAndLongitude(
                    customer.getLocation().getLatitude(), customer.getLocation().getLongitude(),
                    warehouse.getLocation().getLatitude(), warehouse.getLocation().getLongitude());
            Distance distance = new Distance(null, distanceValue, customer, warehouse);
            distances.add(distance);
        }));
        distanceRepository.saveAll(distances);
    }

    public CustomerDto readById(final Long id) throws ResourceNotFoundException {
        return customerRepository.findById(id).map(
                customer -> objectMapper.convertValue(customer, CustomerDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id = " + id + " doesn't exist"));
    }

    public Page<CustomerDto> readPage(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customer -> objectMapper.convertValue(customer, CustomerDto.class));
    }

    @Transactional
    public CustomerDto updateName(final Long id, final String newName)
            throws ResourceNotFoundException {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id = " + id + " doesn't exist"));
        checkInDbByName(newName);
        customer.setName(newName);
        Customer savedCustomer = customerRepository.save(customer);
        return objectMapper.convertValue(savedCustomer, CustomerDto.class);
    }

    @Transactional
    public void deleteById(final Long id) {
        customerRepository.findById(id).ifPresent(customer -> {
            distanceRepository.deleteByCustomerId(id);
            customerRepository.deleteById(id);
        });

    }

    private void checkInDbByName(final String customerName) {
        customerRepository.readByName(customerName).ifPresent(customer -> {
            throw new UnprocessableEntityException(CUSTOMER_NAME_SHOULD_BE_UNIQUE_LITERAL);
        });
    }
}
