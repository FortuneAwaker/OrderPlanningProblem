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
public class DistanceDto {

    @Min(value = 1, message = "Id can't be less than 1!")
    private Long id;
    @NotNull(message = "Distance is mandatory")
    @DecimalMin(value = "0.0", message = "Distance can't be less than 0!")
    private Double distanceValue;
    @Valid
    private WarehouseDto warehouse;
    @Valid
    private CustomerDto customer;

}
