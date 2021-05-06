package com.itechart.orderplanningproblem.controller;

import com.itechart.orderplanningproblem.dto.WarehouseDtoWithId;
import com.itechart.orderplanningproblem.dto.WarehouseDtoWithoutId;
import com.itechart.orderplanningproblem.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseDtoWithId createWarehouse(@Valid @RequestBody WarehouseDtoWithoutId warehouseDtoWithoutId) {
        return warehouseService.create(warehouseDtoWithoutId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<WarehouseDtoWithId> getPage(
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return warehouseService.readPage(pageable);
    }

    @GetMapping("/{id}")
    public WarehouseDtoWithId getById(@PathVariable Long id) {
        return warehouseService.readById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        warehouseService.deleteById(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByIdentifier(@RequestParam String identifier) {
        warehouseService.deleteByIdentifier(identifier);
    }

}
