package com.itechart.orderplanningproblem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistanceWithIdDto {

    private Long id;
    private Double distanceValue;
    private WarehouseDtoWithId warehouse;
    private CustomerDtoWithId customer;

}
