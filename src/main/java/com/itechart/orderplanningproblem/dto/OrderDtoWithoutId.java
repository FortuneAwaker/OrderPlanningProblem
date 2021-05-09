package com.itechart.orderplanningproblem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDtoWithoutId {

    @NotNull(message = "Amount is mandatory")
    @DecimalMin(value = "0.0", message = "Amount can't be less than 0!")
    private Double amount;
    @Valid
    private ItemDtoWithoutId item;
    @NotNull(message = "Customer id is mandatory!")
    @Min(value = 1, message = "Id can't be less than 1!")
    private Long customerId;

}
