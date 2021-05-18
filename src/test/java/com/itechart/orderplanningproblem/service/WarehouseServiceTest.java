package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.WarehouseDto;
import com.itechart.orderplanningproblem.entity.Warehouse;
import com.itechart.orderplanningproblem.error.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.error.exception.UnprocessableEntityException;
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
class WarehouseServiceTest {
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private DistanceRepository distanceRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private WarehouseService warehouseService;

    @Test
    void WarehouseIdentifierToEdit_EditWarehouseWithIdThatDoesNotExist_ThrowResourceNotFoundException() {
        // given
        Long warehouseId = 12503L;
        String warehouseNewIdentifier = "New identifier";
        // when
        Mockito.when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());
        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> warehouseService
                .updateIdentifier(warehouseId, warehouseNewIdentifier));

    }

    @Test
    void WarehouseIdentifierToEdit_EditWarehouseWithExistentIdentifier_ThrowUnprocessableEntityException() {
        // given
        Long warehouseId = 1L;
        String warehouseNewIdentifier = "New identifier";
        Warehouse warehouseInDbById = Warehouse.builder()
                .id(warehouseId)
                .identifier("Old warehouse identifier")
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        Warehouse warehouseInDbByIdentifier = Warehouse.builder()
                .id(2L)
                .identifier(warehouseNewIdentifier)
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        // when
        Mockito.when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouseInDbById));
        Mockito.when(warehouseRepository.readByIdentifier(warehouseNewIdentifier))
                .thenReturn(Optional.of(warehouseInDbByIdentifier));
        // then
        Assertions.assertThrows(UnprocessableEntityException.class,
                () -> warehouseService.updateIdentifier(warehouseId, warehouseNewIdentifier));

    }

    @Test
    void WarehouseIdentifierToEdit_EditWarehouse_ReturnEditedWarehouse()
            throws ResourceNotFoundException, UnprocessableEntityException {
        // given
        Long warehouseId = 1L;
        String warehouseNewIdentifier = "New identifier";
        Warehouse warehouseInDbById = Warehouse.builder()
                .id(warehouseId)
                .identifier("Old warehouse identifier")
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        Warehouse warehouseInDbAfterIdentifierWasChanged = Warehouse.builder()
                .id(warehouseId)
                .identifier(warehouseNewIdentifier)
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        WarehouseDto warehouseDto = WarehouseDto.builder()
                .id(warehouseId)
                .identifier(warehouseNewIdentifier)
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        // when
        Mockito.when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouseInDbById));
        Mockito.when(warehouseRepository.readByIdentifier(warehouseNewIdentifier)).thenReturn(Optional.empty());
        Mockito.when(warehouseRepository.save(warehouseInDbById)).thenReturn(warehouseInDbAfterIdentifierWasChanged);
        Mockito.when(objectMapper.convertValue(warehouseInDbAfterIdentifierWasChanged, WarehouseDto.class))
                .thenReturn(warehouseDto);
        // then
        Assertions.assertEquals(warehouseDto, warehouseService
                .updateIdentifier(warehouseId, warehouseNewIdentifier));

    }

    @Test
    void WarehouseId_FindByIdWarehouseThatDoesNotExist_ThrowResourceNotFoundException() {
        // given
        Long warehouseId = 12503L;
        // when
        Mockito.when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());
        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> warehouseService.readById(warehouseId));

    }

    @Test
    void WarehouseId_FindByIdWarehouse_ReturnWarehouse() throws ResourceNotFoundException {
        // given
        Long warehouseId = 1L;
        String warehouseIdentifier = "Warehouse";
        Warehouse warehouse = Warehouse.builder()
                .id(warehouseId)
                .identifier(warehouseIdentifier)
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        WarehouseDto warehouseDto = WarehouseDto.builder()
                .id(warehouseId)
                .identifier(warehouseIdentifier)
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        // when
        Mockito.when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
        Mockito.when(objectMapper.convertValue(warehouse, WarehouseDto.class))
                .thenReturn(warehouseDto);

        // then
        Assertions.assertEquals(warehouseDto, warehouseService.readById(warehouseId));

    }

    @Test
    void ReadPageOfWarehouses_ReturnPageOfWarehouses() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Long warehouseId = 1L;
        String warehouseIdentifier = "Warehouse";
        Warehouse warehouse = Warehouse.builder()
                .id(warehouseId)
                .identifier(warehouseIdentifier)
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        WarehouseDto warehouseDto = WarehouseDto.builder()
                .id(warehouseId)
                .identifier(warehouseIdentifier)
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        List<Warehouse> warehouseList = Collections.singletonList(warehouse);
        List<WarehouseDto> warehouseDtoList = Collections.singletonList(warehouseDto);
        Page<Warehouse> warehousePage = new PageImpl<>(warehouseList);
        Page<WarehouseDto> warehouseDtoWithIdPage = new PageImpl<>(warehouseDtoList);
        // when
        Mockito.when(warehouseRepository.findAll(pageRequest)).thenReturn(warehousePage);
        Mockito.when(objectMapper.convertValue(warehouse, WarehouseDto.class))
                .thenReturn(warehouseDto);
        // then
        Assertions.assertEquals(warehouseDtoWithIdPage, warehouseService.readPage(pageRequest));

    }

    @Test
    void WarehouseToCreate_CreateWarehouse_ReturnCreatedWarehouse() throws UnprocessableEntityException {
        // given
        Long warehouseId = 1L;
        String warehouseIdentifier = "Warehouse";
        Warehouse warehouse = Warehouse.builder()
                .identifier(warehouseIdentifier)
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        Warehouse createdWarehouse = Warehouse.builder()
                .id(warehouseId)
                .identifier(warehouseIdentifier)
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        WarehouseDto warehouseDtoToBeCreated = WarehouseDto.builder()
                .identifier(warehouseIdentifier)
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        WarehouseDto createdWarehouseDto = WarehouseDto.builder()
                .id(warehouseId)
                .identifier(warehouseIdentifier)
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        // when
        Mockito.when(warehouseRepository.readByIdentifier(warehouseIdentifier)).thenReturn(Optional.empty());
        Mockito.when(objectMapper.convertValue(warehouseDtoToBeCreated, Warehouse.class))
                .thenReturn(warehouse);
        Mockito.when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(distanceRepository.saveAll(new ArrayList<>())).thenReturn(Collections.emptyList());
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(createdWarehouse);
        Mockito.when(objectMapper.convertValue(createdWarehouse, WarehouseDto.class))
                .thenReturn(createdWarehouseDto);

        // then
        Assertions.assertEquals(createdWarehouseDto, warehouseService.create(warehouseDtoToBeCreated));

    }
}
