package com.itechart.orderplanningproblem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class WarehouseItemDtoWithoutId {

    @NotNull(message = "Amount is mandatory")
    @DecimalMin(value = "0.0", message = "Amount can't be less than 0!")
    private Double amount;
    @Valid
    private ItemDtoWithoutId item;

}
