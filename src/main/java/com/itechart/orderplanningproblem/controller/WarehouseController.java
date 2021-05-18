package com.itechart.orderplanningproblem.controller;

import com.itechart.orderplanningproblem.dto.WarehouseDto;
import com.itechart.orderplanningproblem.dto.WarehouseItemChangeAmountDto;
import com.itechart.orderplanningproblem.dto.WarehouseItemDto;
import com.itechart.orderplanningproblem.error.exception.ConflictWithCurrentWarehouseStateException;
import com.itechart.orderplanningproblem.error.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.error.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
@Validated
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseDto createWarehouse(@Valid @RequestBody WarehouseDto warehouseDto)
            throws UnprocessableEntityException {
        warehouseDto.setId(null);
        return warehouseService.create(warehouseDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WarehouseDto updateWarehouseName(
            @Min(value = 1, message = "id must be more or equals 1")
            @PathVariable Long id,
            @Pattern(regexp = "^[A-Z][0-9A-Za-z\\s-]*$",
                    message = "Identifier should match pattern ^[A-Z][0-9A-Za-z\\s-]*$")
            @Size(min = 3, max = 50, message = "Identifier should be longer than 3 letters and shorter than 50.")
            @RequestParam String newName)
            throws ResourceNotFoundException, UnprocessableEntityException {
        return warehouseService.updateName(id, newName);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<WarehouseDto> getPage(
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return warehouseService.readPage(pageable);
    }

    @GetMapping("/{id}")
    public WarehouseDto getById(
            @Min(value = 1, message = "id must be more or equals 1")
            @PathVariable Long id) throws ResourceNotFoundException {
        return warehouseService.readById(id);
    }

    @PutMapping("/{warehouseId}/item")
    public WarehouseDto putItemToWarehouse(
            @Valid @RequestBody WarehouseItemDto warehouseItemDto,
            @Min(value = 1, message = "id must be more or equals 1")
            @PathVariable Long warehouseId,
            @RequestParam String operation)
            throws ResourceNotFoundException, ConflictWithCurrentWarehouseStateException, UnprocessableEntityException {
        return warehouseService.changeAmountOfWarehouseItem(new WarehouseItemChangeAmountDto(warehouseId,
                warehouseItemDto.getAmount(), warehouseItemDto.getItem(), operation));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
            @Min(value = 1, message = "id must be more or equals 1")
            @PathVariable Long id) {
        warehouseService.deleteById(id);
    }

}
