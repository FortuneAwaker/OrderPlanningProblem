package com.itechart.orderplanningproblem.exception;

public class ConflictWithCurrentWarehouseStateException extends Exception {

    public ConflictWithCurrentWarehouseStateException(final String message) {
        super(message);
    }

}