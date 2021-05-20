package com.itechart.orderplanningproblem.controller;

import com.itechart.orderplanningproblem.dto.CustomerDto;
import com.itechart.orderplanningproblem.error.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.error.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.service.CustomerService;
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
@RequestMapping("/api/v1/customers")
@Validated
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto createCustomer(@Valid @RequestBody CustomerDto customerDto)
            throws UnprocessableEntityException {
        customerDto.setId(null);
        return customerService.create(customerDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDto updateCustomerName(
            @Min(value = 1, message = "id must be more or equals 1")
            @PathVariable Long id,
            @Pattern(regexp = "^[A-Z][0-9A-Za-z\\s-]*$", message = "Name should match pattern ^[A-Z][0-9A-Za-z\\s-]*$")
            @Size(min = 3, max = 50, message = "Name should be longer than 3 letters and shorter than 50.")
            @RequestParam String newName)
            throws ResourceNotFoundException, UnprocessableEntityException {
        return customerService.updateName(id, newName);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<CustomerDto> getPage(
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return customerService.readPage(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDto getById(@PathVariable @Min(value = 1,
            message = "id must be more or equals 1") Long id) throws ResourceNotFoundException {
        return customerService.readById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable @Min(value = 1,
            message = "id must be more or equals 1") Long id) {
        customerService.deleteById(id);
    }

}
