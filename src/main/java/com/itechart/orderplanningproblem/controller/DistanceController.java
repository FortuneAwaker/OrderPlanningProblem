package com.itechart.orderplanningproblem.controller;

import com.itechart.orderplanningproblem.dto.DistanceDto;
import com.itechart.orderplanningproblem.error.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.service.DistanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/distance")
@Validated
public class DistanceController {

    private final DistanceService distanceService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DistanceDto getById(
            @Min(value = 1, message = "id must be more or equals 1")
            @PathVariable Long id) throws ResourceNotFoundException {
        return distanceService.readById(id);
    }

}
