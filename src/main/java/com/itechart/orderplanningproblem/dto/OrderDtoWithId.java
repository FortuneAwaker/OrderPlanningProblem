package com.itechart.orderplanningproblem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDtoWithId {

    private Long id;
    private Double amount;
    private Double distance;
    private ItemDtoWithId item;
    private CustomerDtoWithId customer;
    private WarehouseDtoWithId warehouse;

}
