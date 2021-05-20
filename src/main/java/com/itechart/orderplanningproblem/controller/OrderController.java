package com.itechart.orderplanningproblem.controller;

import com.itechart.orderplanningproblem.dto.OrderDto;
import com.itechart.orderplanningproblem.dto.CreateOrderDto;
import com.itechart.orderplanningproblem.error.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.error.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.service.OrderService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@Valid @RequestBody CreateOrderDto createOrderDto)
            throws UnprocessableEntityException, ResourceNotFoundException {
        return orderService.create(createOrderDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderDto> getPage(
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.readPage(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto getById(
            @Min(value = 1, message = "id must be more or equals 1")
            @PathVariable Long id) throws ResourceNotFoundException {
        return orderService.readById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
            @Min(value = 1, message = "id must be more or equals 1")
            @PathVariable Long id) {
        orderService.deleteById(id);
    }

}
