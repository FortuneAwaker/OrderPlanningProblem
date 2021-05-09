package com.itechart.orderplanningproblem.controller;

import com.itechart.orderplanningproblem.dto.CustomerDtoWithId;
import com.itechart.orderplanningproblem.dto.CustomerDtoWithoutId;
import com.itechart.orderplanningproblem.dto.ItemDtoWithId;
import com.itechart.orderplanningproblem.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.service.CustomerService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDtoWithId createItem(@PathVariable Long id,
                                        @RequestParam String newName)
            throws ResourceNotFoundException, UnprocessableEntityException {
        return customerService.updateName(id, newName);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<CustomerDtoWithId> getPage(
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return customerService.readPage(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDtoWithId getById(@PathVariable Long id) throws ResourceNotFoundException {
        return customerService.readById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        customerService.deleteById(id);
    }

}
