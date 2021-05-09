package com.itechart.orderplanningproblem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class WarehouseDtoWithoutId {

    @NotNull(message = "Warehouse identifier is mandatory!")
    @Pattern(regexp = "^[A-Z][0-9A-Za-z\\s-]*$", message = "Identifier should match pattern ^[A-Z][0-9A-Za-z\\s-]*$")
    @Size(min = 3, max = 50, message = "Identifier should be longer than 3 letters and shorter than 50.")
    private String identifier;
    @NotNull(message = "Latitude is mandatory!")
    @DecimalMin(value = "-90.0", message = "Latitude can't be less than -90.0!")
    @DecimalMax(value = "90.0", message = "Latitude can't be more than 90.0!")
    private Double latitude;
    @NotNull(message = "Longitude is mandatory!")
    @DecimalMin(value = "-180.0", message = "Longitude can't be less than -180.0!")
    @DecimalMax(value = "180.0", message = "Longitude can't be less more 180.0!")
    private Double longitude;
    private List<@Valid WarehouseItemDtoWithoutId> items = new ArrayList<>();

}
