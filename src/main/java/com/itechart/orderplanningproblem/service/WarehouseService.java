package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.WarehouseDto;
import com.itechart.orderplanningproblem.dto.WarehouseItemChangeAmountDto;
import com.itechart.orderplanningproblem.entity.Customer;
import com.itechart.orderplanningproblem.entity.Distance;
import com.itechart.orderplanningproblem.entity.Item;
import com.itechart.orderplanningproblem.entity.Warehouse;
import com.itechart.orderplanningproblem.entity.WarehouseItem;
import com.itechart.orderplanningproblem.exception.ConflictWithCurrentWarehouseStateException;
import com.itechart.orderplanningproblem.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.exception.UnprocessableEntityException;
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
import java.util.Optional;

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
        checkInDbByIdentifier(warehouseDto.getIdentifier());
        Warehouse warehouseFromDto = objectMapper.convertValue(warehouseDto, Warehouse.class);
        mapWarehouseItems(warehouseFromDto);
        Warehouse createdWarehouse = warehouseRepository.save(warehouseFromDto);
        mapWarehouseToExistentCustomers(createdWarehouse);
        return objectMapper.convertValue(createdWarehouse, WarehouseDto.class);
    }

    private void mapWarehouseToExistentCustomers(final Warehouse warehouse) {
        List<Customer> allCustomers = customerRepository.findAll();
        List<Distance> distances = new ArrayList<>();
        for (Customer customer: allCustomers) {
            double distanceValue = distanceService.getDistanceByLatitudeAndLongitude(
                    customer.getLatitude(), customer.getLongitude(),
                    warehouse.getLatitude(), warehouse.getLongitude());
            Distance distance = new Distance(null, distanceValue, customer, warehouse);
            distances.add(distance);
        }
        distanceRepository.saveAll(distances);
    }

    @Transactional
    public WarehouseDto updateIdentifier(final Long id, final String newIdentifier)
            throws ResourceNotFoundException, UnprocessableEntityException {
        Warehouse warehouse = findWarehouseById(id);
        checkInDbByIdentifier(newIdentifier);
        warehouse.setIdentifier(newIdentifier);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return objectMapper.convertValue(savedWarehouse, WarehouseDto.class);
    }

    @Transactional
    public WarehouseDto changeAmountOfWarehouseItem(
            final WarehouseItemChangeAmountDto warehouseItemChangeAmountDto)
            throws ResourceNotFoundException, UnprocessableEntityException, ConflictWithCurrentWarehouseStateException {
        if (warehouseItemChangeAmountDto.getOperation().equals("put")) {
            return increaseAmountOfWarehouseItem(warehouseItemChangeAmountDto);
        }
        if (warehouseItemChangeAmountDto.getOperation().equals("remove")) {
            return decreaseAmountOfWarehouseItem(warehouseItemChangeAmountDto);
        }
        throw new UnprocessableEntityException("Operation must have value <put> or <remove>!");
    }


    private WarehouseDto increaseAmountOfWarehouseItem(
            final WarehouseItemChangeAmountDto warehouseItemChangeAmountDto) throws ResourceNotFoundException {
        Warehouse warehouse = findWarehouseById(warehouseItemChangeAmountDto.getWarehouseId());
        boolean processed = false;
        for (WarehouseItem item : warehouse.getItems()
        ) {
            if (item.getItem().getName().equals(warehouseItemChangeAmountDto.getItem().getName())) {
                item.setAmount(item.getAmount() + warehouseItemChangeAmountDto.getAmount());
                processed = true;
                break;
            }
        }
        if (!processed) {
            Item itemToSave = objectMapper.convertValue(warehouseItemChangeAmountDto.getItem(), Item.class);
            findByNameOrCreateItemToPersist(itemToSave);
            warehouse.getItems().add(new WarehouseItem(null, warehouseItemChangeAmountDto.getAmount(),
                    itemToSave, warehouse));
        }
        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return objectMapper.convertValue(updatedWarehouse, WarehouseDto.class);
    }

    private WarehouseDto decreaseAmountOfWarehouseItem(
            final WarehouseItemChangeAmountDto warehouseItemChangeAmountDto)
            throws ResourceNotFoundException, ConflictWithCurrentWarehouseStateException {
        Warehouse warehouse = findWarehouseById(warehouseItemChangeAmountDto.getWarehouseId());
        boolean processed = false;
        for (WarehouseItem item : warehouse.getItems()
        ) {
            if (item.getItem().getName().equals(warehouseItemChangeAmountDto.getItem().getName())) {
                if (item.getAmount() >= warehouseItemChangeAmountDto.getAmount()) {
                    item.setAmount(item.getAmount() - warehouseItemChangeAmountDto.getAmount());
                    if (item.getAmount() == 0) {
                        warehouse.getItems().remove(item);
                    }
                    processed = true;
                    break;
                } else {
                    throw new ConflictWithCurrentWarehouseStateException("It is impossible to remove more items " +
                            "than are in the warehouse! Current value of item with name " + item.getItem().getName()
                            + " is " + item.getAmount() + ".");
                }
            }
        }
        if (!processed) {
            throw new ResourceNotFoundException("There is no item with such name in warehouse with id "
                    + warehouse.getId());
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
        if (warehouseRepository.findById(id).isEmpty()) {
            return;
        }
        distanceRepository.deleteByWarehouseId(id);
        warehouseRepository.deleteById(id);
    }

    @Transactional
    public void deleteByIdentifier(final String identifier) {
        Optional<Warehouse> warehouse = warehouseRepository.readByIdentifier(identifier);
        if (warehouse.isEmpty()) {
            return;
        }
        distanceRepository.deleteByWarehouseId(warehouse.get().getId());
        warehouseRepository.deleteById(warehouse.get().getId());
    }

    private void mapWarehouseItems(Warehouse warehouse) {
        for (WarehouseItem item : warehouse.getItems()) {
            item.setWarehouse(warehouse);
            findByNameOrCreateItemToPersist(item.getItem());
        }
    }

    private void findByNameOrCreateItemToPersist(Item item) {
        Optional<Item> foundInDb = itemRepository.readByName(item.getName());
        if (foundInDb.isPresent()) {
            item.setId(foundInDb.get().getId());
        } else {
            itemRepository.save(item);
        }
    }

    private Warehouse findWarehouseById(final Long warehouseId) throws ResourceNotFoundException {
        Optional<Warehouse> fromDbById = warehouseRepository.findById(warehouseId);
        if (fromDbById.isEmpty()) {
            throw new ResourceNotFoundException("Warehouse with id = " + warehouseId + " doesn't exist");
        }
        return fromDbById.get();
    }

    private void checkInDbByIdentifier(final String identifier) throws UnprocessableEntityException {
        Optional<Warehouse> fromDbByIdentifier = warehouseRepository.readByIdentifier(identifier);
        if (fromDbByIdentifier.isPresent()) {
            throw new UnprocessableEntityException(WAREHOUSE_IDENTIFIER_SHOULD_BE_UNIQUE_LITERAL);
        }
    }

}
