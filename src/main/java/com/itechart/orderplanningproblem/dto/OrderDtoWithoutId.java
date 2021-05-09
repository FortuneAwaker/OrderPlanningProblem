package com.itechart.orderplanningproblem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDtoWithoutId {

    private Double amount;
    private ItemDtoWithoutId item;
    private Long customerId;

}
