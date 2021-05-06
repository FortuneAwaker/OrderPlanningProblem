package com.itechart.orderplanningproblem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class WarehouseDtoWithId {

    private Long id;
    private String identifier;
    private Double latitude;
    private Double longitude;
    private List<WarehouseItemDtoWithId> items = new ArrayList<>();

}
