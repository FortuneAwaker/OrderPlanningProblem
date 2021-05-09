package com.itechart.orderplanningproblem.service;

import org.springframework.stereotype.Service;

@Service
public class DistanceService {

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
