package com.itechart.orderplanningproblem.error;

import com.itechart.orderplanningproblem.dto.ExceptionDto;
import com.itechart.orderplanningproblem.error.exception.ConflictWithCurrentWarehouseStateException;
import com.itechart.orderplanningproblem.error.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.error.exception.UnprocessableEntityException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ControllerAdvice
public class AdviceController extends ResponseEntityExceptionHandler {

    private static final String EXCEPTION_MESSAGE = "Please, provide valid input data!";

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ExceptionDto> handleRunTimeException(final RuntimeException exception) {
        return new ResponseEntity<>(
                new ExceptionDto(INTERNAL_SERVER_ERROR.value(), exception.getMessage(),
                        Timestamp.valueOf(LocalDateTime.now()).toString()), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ExceptionDto> handleDataIntegrityViolationException(
            final DataIntegrityViolationException exception) {
        return new ResponseEntity<>(
                new ExceptionDto(UNPROCESSABLE_ENTITY.value(),
                        "Operation can't be performed, because it will violate data integrity.",
                        Timestamp.valueOf(LocalDateTime.now()).toString()), UNPROCESSABLE_ENTITY);
    }


    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex,
            HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        return new ResponseEntity<>(buildException(BAD_REQUEST.value(),
                ex.getClass().getName() + ": " + EXCEPTION_MESSAGE), BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return new ResponseEntity<>(buildException(BAD_REQUEST.value(),
                ex.getClass().getName() + ": " + EXCEPTION_MESSAGE), BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return new ResponseEntity<>(buildException(BAD_REQUEST.value(),
                ex.getClass().getName() + ": " + EXCEPTION_MESSAGE), BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return new ResponseEntity<>(buildException(BAD_REQUEST.value(),
                ex.getClass().getName() + ": " + EXCEPTION_MESSAGE), BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return new ResponseEntity<>(buildException(BAD_REQUEST.value(),
                ex.getClass().getName() + ": " + EXCEPTION_MESSAGE), BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<String> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        return new ResponseEntity<>(buildException(BAD_REQUEST.value(), validationErrors.toString()), BAD_REQUEST);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ExceptionDto> handleConstraintViolationException(final ConstraintViolationException e) {
        return new ResponseEntity<>(buildException(BAD_REQUEST.value(), e.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpClientErrorException.class})
    public ResponseEntity<ExceptionDto> handleHttpClientErrorException(final HttpClientErrorException e) {
        return new ResponseEntity<>(buildException(BAD_REQUEST.value(), e.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(value = {NumberFormatException.class})
    public ResponseEntity<ExceptionDto> handleWrongUsersInputException(final NumberFormatException e) {
        return new ResponseEntity<>(buildException(BAD_REQUEST.value(), EXCEPTION_MESSAGE), BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ExceptionDto> handleNotFoundException(final ResourceNotFoundException e) {
        return new ResponseEntity<>(buildException(NOT_FOUND.value(), e.getMessage()), NOT_FOUND);
    }

    @ExceptionHandler({UnprocessableEntityException.class})
    public ResponseEntity<ExceptionDto> handleNotUnprocessableEntity(final UnprocessableEntityException e) {
        return new ResponseEntity<>(buildException(UNPROCESSABLE_ENTITY.value(), e.getMessage()), UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({ConflictWithCurrentWarehouseStateException.class})
    public ResponseEntity<ExceptionDto> handleConflictWithCurrentWarehouseStateException(
            final ConflictWithCurrentWarehouseStateException e) {
        return new ResponseEntity<>(buildException(UNPROCESSABLE_ENTITY.value(), e.getMessage()), UNPROCESSABLE_ENTITY);
    }

    private ExceptionDto buildException(final int errorCode, final String message) {
        return new ExceptionDto(errorCode, message, Timestamp.valueOf(LocalDateTime.now()).toString());
    }

}
