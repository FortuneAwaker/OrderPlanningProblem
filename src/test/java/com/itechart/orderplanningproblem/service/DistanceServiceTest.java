package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.CustomerDto;
import com.itechart.orderplanningproblem.dto.DistanceDto;
import com.itechart.orderplanningproblem.dto.LocationDto;
import com.itechart.orderplanningproblem.dto.WarehouseDto;
import com.itechart.orderplanningproblem.entity.Customer;
import com.itechart.orderplanningproblem.entity.Distance;
import com.itechart.orderplanningproblem.entity.Location;
import com.itechart.orderplanningproblem.entity.Warehouse;
import com.itechart.orderplanningproblem.error.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.repository.DistanceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DistanceServiceTest {

    @Mock
    private DistanceRepository distanceRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DistanceService distanceService;

    @Test
    void LatitudesAndLongitudes_CalculateDistance_ReturnDistance() {
        // given
        Double firstLatitude = 55.0055;
        Double firstLongitude = 24.454732;
        Double secondLatitude = 22.12345;
        Double secondLongitude = 54.6688;
        Double distanceValue = 4432.186613415447;
        // when
        Double foundDistance = distanceService.getDistanceByLatitudeAndLongitude(
                firstLatitude, firstLongitude, secondLatitude, secondLongitude);
        // then
        Assertions.assertEquals(distanceValue, foundDistance);

    }

    @Test
    void DistanceId_FindByIdDistanceThatDoesNotExist_ThrowResourceNotFoundException() {
        // given
        Long distanceId = 12503L;
        // when
        Mockito.when(distanceRepository.findById(distanceId)).thenReturn(Optional.empty());
        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> distanceService.readById(distanceId));

    }

    @Test
    void DistanceId_FindByIdDistance_ReturnDistance() throws ResourceNotFoundException {
        // given
        Long distanceId = 1L;
        Double distanceValue = 4432.186613415447;
        Location customerLocation = Location.builder()
                .latitude(55.0055)
                .longitude(24.454732)
                .build();
        LocationDto customerLocationDto = LocationDto.builder()
                .latitude(55.0055)
                .longitude(24.454732)
                .build();
        Location warehouseLocation = Location.builder()
                .latitude(22.12345)
                .longitude(54.6688)
                .build();
        LocationDto warehouseLocationDto = LocationDto.builder()
                .latitude(22.12345)
                .longitude(54.6688)
                .build();
        Customer customer = Customer.builder()
                .id(1L)
                .name("Customer")
                .location(customerLocation)
                .build();
        CustomerDto customerDto = CustomerDto.builder()
                .id(1L)
                .name("Customer")
                .location(customerLocationDto)
                .build();
        Warehouse warehouse = Warehouse.builder()
                .id(1L)
                .name("Warehouse")
                .location(warehouseLocation)
                .items(new ArrayList<>())
                .build();
        WarehouseDto warehouseDto = WarehouseDto.builder()
                .id(1L)
                .name("Warehouse")
                .location(warehouseLocationDto)
                .items(new ArrayList<>())
                .build();
        Distance distance = Distance.builder()
                .id(distanceId)
                .distanceValue(distanceValue)
                .customer(customer)
                .warehouse(warehouse)
                .build();
        DistanceDto distanceDto = DistanceDto.builder()
                .id(distanceId)
                .distanceValue(distanceValue)
                .customer(customerDto)
                .warehouse(warehouseDto)
                .build();
        // when
        Mockito.when(distanceRepository.findById(distanceId)).thenReturn(Optional.of(distance));
        Mockito.when(objectMapper.convertValue(distance, DistanceDto.class))
                .thenReturn(distanceDto);

        // then
        Assertions.assertEquals(distanceDto, distanceService.readById(distanceId));

    }
}
