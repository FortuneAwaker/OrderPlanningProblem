package com.itechart.orderplanningproblem.controller;

import com.itechart.orderplanningproblem.dto.ItemDto;
import com.itechart.orderplanningproblem.error.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.error.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.service.ItemService;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto) throws UnprocessableEntityException {
        itemDto.setId(null);
        return itemService.create(itemDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItemName(
            @Min(value = 1, message = "id must be more or equals 1")
            @PathVariable Long id,
            @Pattern(regexp = "^[A-Z][0-9A-Za-z\\s-]*$", message = "Name should match pattern ^[A-Z][0-9A-Za-z\\s-]*$")
            @Size(min = 3, max = 50, message = "Name should be longer than 3 letters and shorter than 50.")
            @RequestParam String newName)
            throws ResourceNotFoundException, UnprocessableEntityException {
        return itemService.updateName(id, newName);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ItemDto> getPage(
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return itemService.readPage(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getById(
            @Min(value = 1, message = "id must be more or equals 1")
            @PathVariable Long id) throws ResourceNotFoundException {
        return itemService.readById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
            @Min(value = 1, message = "id must be more or equals 1")
            @PathVariable Long id) {
        itemService.deleteById(id);
    }

}
