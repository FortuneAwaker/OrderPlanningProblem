package com.itechart.orderplanningproblem.controller;

import com.itechart.orderplanningproblem.dto.OrderDtoWithId;
import com.itechart.orderplanningproblem.dto.OrderDtoWithoutId;
import com.itechart.orderplanningproblem.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDtoWithId createItem(@Valid @RequestBody OrderDtoWithoutId orderDtoWithoutId)
            throws UnprocessableEntityException, ResourceNotFoundException {
        return orderService.create(orderDtoWithoutId);
    }

}
