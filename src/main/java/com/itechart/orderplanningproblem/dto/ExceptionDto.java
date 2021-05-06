package com.itechart.orderplanningproblem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionDto {

    private Integer statusCode;
    private String message;
    private String timestamp;

}
