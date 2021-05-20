package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.Operation;
import com.itechart.orderplanningproblem.dto.WarehouseDto;
import com.itechart.orderplanningproblem.dto.WarehouseItemChangeAmountDto;
import com.itechart.orderplanningproblem.entity.Customer;
import com.itechart.orderplanningproblem.entity.Distance;
import com.itechart.orderplanningproblem.entity.Item;
import com.itechart.orderplanningproblem.entity.Warehouse;
import com.itechart.orderplanningproblem.entity.WarehouseItem;
import com.itechart.orderplanningproblem.error.exception.ConflictWithCurrentWarehouseStateException;
import com.itechart.orderplanningproblem.error.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.error.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.repository.CustomerRepository;
import com.itechart.orderplanningproblem.repository.DistanceRepository;
import com.itechart.orderplanningproblem.repository.ItemRepository;
import com.itechart.orderplanningproblem.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final DistanceRepository distanceRepository;
    private final ObjectMapper objectMapper;
    private final DistanceService distanceService;

    private static final String WAREHOUSE_IDENTIFIER_SHOULD_BE_UNIQUE_LITERAL = "Warehouse with such identifier" +
            " already exists. Warehouse identifier should be unique!";

    @Transactional
    public WarehouseDto create(final WarehouseDto warehouseDto) throws UnprocessableEntityException {
        checkInDbByName(warehouseDto.getName());
        Warehouse warehouseFromDto = objectMapper.convertValue(warehouseDto, Warehouse.class);
        mapWarehouseItems(warehouseFromDto);
        Warehouse createdWarehouse = warehouseRepository.save(warehouseFromDto);
        mapWarehouseToExistentCustomers(createdWarehouse);
        return objectMapper.convertValue(createdWarehouse, WarehouseDto.class);
    }

    private void mapWarehouseToExistentCustomers(final Warehouse warehouse) {
        List<Customer> allCustomers = customerRepository.findAll();
        List<Distance> distances = allCustomers.stream().map(customer -> {
            double distanceValue = distanceService.getDistanceByLatitudeAndLongitude(
                    customer.getLocation().getLatitude(), customer.getLocation().getLongitude(),
                    warehouse.getLocation().getLatitude(), warehouse.getLocation().getLongitude());
            return new Distance(null, distanceValue, customer, warehouse);
        }).collect(Collectors.toList());
        distanceRepository.saveAll(distances);
    }

    @Transactional
    public WarehouseDto updateName(final Long id, final String newName)
            throws ResourceNotFoundException, UnprocessableEntityException {
        Warehouse warehouse = findWarehouseById(id);
        checkInDbByName(newName);
        warehouse.setName(newName);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return objectMapper.convertValue(savedWarehouse, WarehouseDto.class);
    }

    @Transactional
    public WarehouseDto changeAmountOfWarehouseItem(
            final WarehouseItemChangeAmountDto warehouseItemChangeAmountDto)
            throws ResourceNotFoundException, UnprocessableEntityException, ConflictWithCurrentWarehouseStateException {
        if (warehouseItemChangeAmountDto.getOperation().equals(Operation.PUT)) {
            return increaseAmountOfWarehouseItem(warehouseItemChangeAmountDto);
        }
        if (warehouseItemChangeAmountDto.getOperation().equals(Operation.REMOVE)) {
            return decreaseAmountOfWarehouseItem(warehouseItemChangeAmountDto);
        }
        throw new UnprocessableEntityException("Operation must have value <PUT> or <REMOVE>!");
    }


    private WarehouseDto increaseAmountOfWarehouseItem(
            final WarehouseItemChangeAmountDto warehouseItemChangeAmountDto) throws ResourceNotFoundException {
        Warehouse warehouse = findWarehouseById(warehouseItemChangeAmountDto.getWarehouseId());
        warehouse.getItems().stream()
                .filter(item -> item.getItem().getName().equals(warehouseItemChangeAmountDto.getItem().getName()))
                .findFirst()
                .ifPresentOrElse(
                        whItem -> whItem.setAmount(whItem.getAmount() + warehouseItemChangeAmountDto.getAmount()),

                        () -> {
                            Item itemToSave = objectMapper.convertValue(warehouseItemChangeAmountDto.getItem(),
                                    Item.class);
                            findByNameOrCreateItemToPersist(itemToSave);
                            warehouse.getItems().add(new WarehouseItem(null,
                                    warehouseItemChangeAmountDto.getAmount(), itemToSave, warehouse));
                        });

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return objectMapper.convertValue(updatedWarehouse, WarehouseDto.class);
    }

    private WarehouseDto decreaseAmountOfWarehouseItem(final WarehouseItemChangeAmountDto warehouseItemChangeAmountDto)
            throws ResourceNotFoundException, ConflictWithCurrentWarehouseStateException {
        Warehouse warehouse = findWarehouseById(warehouseItemChangeAmountDto.getWarehouseId());
        Item neededItem = itemRepository.readByName(warehouseItemChangeAmountDto.getItem().getName())
                .orElseThrow(() -> new ResourceNotFoundException("Item with such name doesn't exist"));
        WarehouseItem item = warehouse.getItems().stream()
                .filter(it -> it.getItem().getId().equals(neededItem.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("There is no item with such name in warehouse with id "
                        + warehouse.getId()));

        if (item.getAmount() >= warehouseItemChangeAmountDto.getAmount()) {
            item.setAmount(item.getAmount() - warehouseItemChangeAmountDto.getAmount());
            if (item.getAmount() == 0) {
                warehouse.getItems().remove(item);
            }
        } else {
            throw new ConflictWithCurrentWarehouseStateException("It is impossible to remove more items " +
                    "than are in the warehouse! Current value of item with name " + item.getItem().getName()
                    + " is " + item.getAmount() + ".");
        }

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return objectMapper.convertValue(updatedWarehouse, WarehouseDto.class);
    }

    public WarehouseDto readById(final Long id) throws ResourceNotFoundException {
        return warehouseRepository.findById(id).map(
                warehouse -> objectMapper.convertValue(warehouse, WarehouseDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse with id = " + id + " doesn't exist"));
    }

    public Page<WarehouseDto> readPage(Pageable pageable) {
        return warehouseRepository.findAll(pageable)
                .map(warehouse -> objectMapper.convertValue(warehouse, WarehouseDto.class));
    }

    @Transactional
    public void deleteById(final Long id) {
        warehouseRepository.findById(id).ifPresent(warehouse -> {
            distanceRepository.deleteByWarehouseId(id);
            warehouseRepository.deleteById(id);
        });
    }

    private void mapWarehouseItems(Warehouse warehouse) {
        warehouse.getItems().forEach(item -> {
            item.setWarehouse(warehouse);
            findByNameOrCreateItemToPersist(item.getItem());
        });
    }

    private void findByNameOrCreateItemToPersist(Item item) {
        itemRepository.readByName(item.getName()).ifPresentOrElse(
                foundItem -> item.setId(foundItem.getId()),
                () -> itemRepository.save(item));
    }

    private Warehouse findWarehouseById(final Long warehouseId) throws ResourceNotFoundException {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Warehouse with id = " + warehouseId + " doesn't exist"));
    }

    private void checkInDbByName(final String name) throws UnprocessableEntityException {
        warehouseRepository.readByName(name).ifPresent(warehouse -> {
            throw new UnprocessableEntityException(WAREHOUSE_IDENTIFIER_SHOULD_BE_UNIQUE_LITERAL);
        });
    }

}
