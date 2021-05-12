package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.CustomerDtoWithId;
import com.itechart.orderplanningproblem.dto.CustomerDtoWithoutId;
import com.itechart.orderplanningproblem.entity.Customer;
import com.itechart.orderplanningproblem.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.repository.CustomerRepository;
import com.itechart.orderplanningproblem.repository.DistanceRepository;
import com.itechart.orderplanningproblem.repository.WarehouseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private DistanceRepository distanceRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private CustomerService customerService;

    @Test
    void CustomerNameToEdit_EditCustomerWithIdThatDoesNotExist_ThrowResourceNotFoundException() {
        // given
        Long customerId = 12503L;
        String customerNewName = "New name";
        // when
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> customerService
                .updateName(customerId, customerNewName));

    }

    @Test
    void CustomerNameToEdit_EditCustomerWithExistentName_ThrowUnprocessableEntityException() {
        // given
        Long customerId = 12503L;
        String customerNewName = "New name";
        Double latitude = 55.0055;
        Double longitude = 24.454732;
        Customer customerInDbById = Customer.builder()
                .id(customerId)
                .name("Old item name")
                .latitude(latitude)
                .longitude(longitude)
                .build();
        Customer customerInDbByName = Customer.builder()
                .id(2L)
                .name(customerNewName)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        // when
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerInDbById));
        Mockito.when(customerRepository.readByName(customerNewName)).thenReturn(Optional.of(customerInDbByName));
        // then
        Assertions.assertThrows(UnprocessableEntityException.class, () -> customerService
                .updateName(customerId, customerNewName));

    }

    @Test
    void CustomerNameToEdit_EditCustomer_ReturnEditedCustomer()
            throws ResourceNotFoundException, UnprocessableEntityException {
        // given
        Long customerId = 1L;
        String customerNewName = "New name";
        Double latitude = 55.0055;
        Double longitude = 24.454732;
        Customer customerInDbById = Customer.builder()
                .id(customerId)
                .name("Old item name")
                .latitude(latitude)
                .longitude(longitude)
                .build();
        Customer customerInDbAfterNameWasChanged = Customer.builder()
                .id(customerId)
                .name(customerNewName)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        CustomerDtoWithId customerDtoWithId = CustomerDtoWithId.builder()
                .id(customerId)
                .name(customerNewName)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        // when
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerInDbById));
        Mockito.when(customerRepository.readByName(customerNewName)).thenReturn(Optional.empty());
        Mockito.when(customerRepository.save(customerInDbById)).thenReturn(customerInDbAfterNameWasChanged);
        Mockito.when(objectMapper.convertValue(customerInDbAfterNameWasChanged, CustomerDtoWithId.class))
                .thenReturn(customerDtoWithId);
        // then
        Assertions.assertEquals(customerDtoWithId, customerService.updateName(customerId, customerNewName));

    }

    @Test
    void CustomerId_FindByIdCustomerThatDoesNotExist_ThrowResourceNotFoundException() {
        // given
        Long customerId = 12503L;
        // when
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> customerService.readById(customerId));

    }

    @Test
    void CustomerId_FindByIdCustomer_ReturnCustomer() throws ResourceNotFoundException {
        // given
        Long customerId = 1L;
        String customerName = "Customer";
        Double latitude = 55.0055;
        Double longitude = 24.454732;
        Customer customer = Customer.builder()
                .id(customerId)
                .name(customerName)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        CustomerDtoWithId customerDto = CustomerDtoWithId.builder()
                .id(customerId)
                .name(customerName)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        // when
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        Mockito.when(objectMapper.convertValue(customer, CustomerDtoWithId.class))
                .thenReturn(customerDto);

        // then
        Assertions.assertEquals(customerDto, customerService.readById(customerId));

    }

    @Test
    void ReadPageOfCustomers_ReturnPageOfCustomers() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Long customerId = 1L;
        String customerName = "Customer";
        Double latitude = 55.0055;
        Double longitude = 24.454732;
        Customer customer = Customer.builder()
                .id(customerId)
                .name(customerName)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        CustomerDtoWithId customerDto = CustomerDtoWithId.builder()
                .id(customerId)
                .name(customerName)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        List<Customer> customerList = Collections.singletonList(customer);
        List<CustomerDtoWithId> customerDtoWithIdList = Collections.singletonList(customerDto);
        Page<Customer> customerPage = new PageImpl<>(customerList);
        Page<CustomerDtoWithId> customerDtoWithIdPage = new PageImpl<>(customerDtoWithIdList);
        // when
        Mockito.when(customerRepository.findAll(pageRequest)).thenReturn(customerPage);
        Mockito.when(objectMapper.convertValue(customer, CustomerDtoWithId.class))
                .thenReturn(customerDto);
        // then
        Assertions.assertEquals(customerDtoWithIdPage, customerService.readPage(pageRequest));

    }

    @Test
    void CustomerToCreate_CreateCustomer_ReturnCreatedCustomer() throws UnprocessableEntityException {
        // given
        Long customerId = 1L;
        String customerName = "Customer";
        Double latitude = 55.0055;
        Double longitude = 24.454732;
        Customer customer = Customer.builder()
                .id(customerId)
                .name(customerName)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        Customer createdCustomer = Customer.builder()
                .id(customerId)
                .name(customerName)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        CustomerDtoWithoutId customerDtoToBeCreated = CustomerDtoWithoutId.builder()
                .name(customerName)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        CustomerDtoWithId createdCustomerDto = CustomerDtoWithId.builder()
                .id(customerId)
                .name(customerName)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        // when
        Mockito.when(customerRepository.readByName(customerName)).thenReturn(Optional.empty());
        Mockito.when(objectMapper.convertValue(customerDtoToBeCreated, Customer.class))
                .thenReturn(customer);
        Mockito.when(warehouseRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(distanceRepository.saveAll(new ArrayList<>())).thenReturn(Collections.emptyList());
        Mockito.when(customerRepository.save(customer)).thenReturn(createdCustomer);
        Mockito.when(objectMapper.convertValue(createdCustomer, CustomerDtoWithId.class))
                .thenReturn(createdCustomerDto);

        // then
        Assertions.assertEquals(createdCustomerDto, customerService.create(customerDtoToBeCreated));

    }

}