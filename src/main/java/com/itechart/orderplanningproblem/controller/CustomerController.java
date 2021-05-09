package com.itechart.orderplanningproblem.controller;

import com.itechart.orderplanningproblem.dto.CustomerDtoWithId;
import com.itechart.orderplanningproblem.dto.CustomerDtoWithoutId;
import com.itechart.orderplanningproblem.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.service.CustomerService;
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
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDtoWithId createItem(@Valid @RequestBody CustomerDtoWithoutId customerDto)
            throws UnprocessableEntityException {
        return customerService.create(customerDto);
    }

}
