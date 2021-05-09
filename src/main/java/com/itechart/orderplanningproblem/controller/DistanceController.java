package com.itechart.orderplanningproblem.controller;

import com.itechart.orderplanningproblem.dto.DistanceWithIdDto;
import com.itechart.orderplanningproblem.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.service.DistanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/distance")
public class DistanceController {

    private final DistanceService distanceService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DistanceWithIdDto getById(@PathVariable Long id) throws ResourceNotFoundException {
        return distanceService.readById(id);
    }

}
