package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.WarehouseDtoWithId;
import com.itechart.orderplanningproblem.dto.WarehouseDtoWithoutId;
import com.itechart.orderplanningproblem.entity.Item;
import com.itechart.orderplanningproblem.entity.Warehouse;
import com.itechart.orderplanningproblem.entity.WarehouseItem;
import com.itechart.orderplanningproblem.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.repository.ItemRepository;
import com.itechart.orderplanningproblem.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final ItemRepository itemRepository;

    private final ObjectMapper objectMapper;

    private static final String WAREHOUSE_IDENTIFIER_SHOULD_BE_UNIQUE_LITERAL = "Warehouse with such identifier" +
            " already exists. Warehouse identifier should be unique!";

    @Transactional
    public WarehouseDtoWithId create(final WarehouseDtoWithoutId warehouseDtoWithoutId) {
        Optional<Warehouse> fromDbByIdentifier = warehouseRepository.readByIdentifier(
                warehouseDtoWithoutId.getIdentifier());
        if (fromDbByIdentifier.isPresent()) {
            throw new UnprocessableEntityException(WAREHOUSE_IDENTIFIER_SHOULD_BE_UNIQUE_LITERAL);
        }
        Warehouse warehouseFromDto = objectMapper.convertValue(warehouseDtoWithoutId, Warehouse.class);
        mapWarehouseItems(warehouseFromDto);
        Warehouse createdWarehouse = warehouseRepository.save(warehouseFromDto);
        return objectMapper.convertValue(createdWarehouse, WarehouseDtoWithId.class);
    }

    public WarehouseDtoWithId readById(final Long id) {
        return warehouseRepository.findById(id).map(item -> objectMapper.convertValue(item, WarehouseDtoWithId.class))
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse with id = " + id + " doesn't exist"));
    }

    public Page<WarehouseDtoWithId> readPage(Pageable pageable) {
        return warehouseRepository.findAll(pageable)
                .map(item -> objectMapper.convertValue(item, WarehouseDtoWithId.class));
    }

    @Transactional
    public void deleteById(final Long id) {
        if (warehouseRepository.findById(id).isEmpty()) {
            return;
        }
        warehouseRepository.deleteById(id);
    }

    @Transactional
    public void deleteByIdentifier(final String identifier) {
        if (warehouseRepository.readByIdentifier(identifier).isEmpty()) {
            return;
        }
        warehouseRepository.deleteByIdentifier(identifier);
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

}
