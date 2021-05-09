package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.DistanceWithIdDto;
import com.itechart.orderplanningproblem.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.repository.DistanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistanceService {

    private final DistanceRepository distanceRepository;
    private final ObjectMapper objectMapper;

    public DistanceWithIdDto readById(final Long id) throws ResourceNotFoundException {
        return distanceRepository.findById(id).map(
                distance -> objectMapper.convertValue(distance, DistanceWithIdDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Distance with id = " + id + " doesn't exist"));
    }

    public Double getDistanceByLatitudeAndLongitude(final Double firstLatitude, final Double firstLongitude,
                                                     final Double secondLatitude, final Double secondLongitude) {
        double radiusOfEarth = 6371;
        double diffLat = degreesToRadians(secondLatitude - firstLatitude);
        double diffLong = degreesToRadians(secondLongitude - firstLongitude);
        double a =
                Math.sin(diffLat/2) * Math.sin(diffLat/2) +
                        Math.cos(degreesToRadians(firstLatitude)) * Math.cos(degreesToRadians(secondLatitude)) *
                                Math.sin(diffLong/2) * Math.sin(diffLong/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return radiusOfEarth * c;
    }

    private Double degreesToRadians(final Double degrees) {
        return degrees * (Math.PI/180);
    }

}
