package com.itechart.orderplanningproblem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {

    @Min(value = 1, message = "Id can't be less than 1!")
    private Long id;
    @NotNull(message = "Item name is mandatory!")
    @Pattern(regexp = "^[A-Z][0-9A-Za-z\\s-]*$", message = "Name should match pattern ^[A-Z][0-9A-Za-z\\s-]*$")
    @Size(min = 3, max = 50, message = "Name should be longer than 3 letters and shorter than 50.")
    private String name;

}
