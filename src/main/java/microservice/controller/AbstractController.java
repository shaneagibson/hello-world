package microservice.controller;

import microservice.model.Error;
import microservice.exception.BadRequestException;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.function.BiConsumer;

public abstract class AbstractController {

    private final Logger log;

    AbstractController(final Logger log) {
        this.log = log;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Error handleRuntimeException(final RuntimeException e) {
        log.error(e.getMessage(), e);
        return new Error(e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error handleBadRequestException(final BadRequestException e) {
        log.warn(e.getMessage(), e);
        return new Error(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.warn(e.getMessage(), e);
        return new Error(e.getMessage());
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    @ResponseBody
    public ResponseEntity<Error> handleHttpStatusCodeException(final HttpStatusCodeException e) {
        final BiConsumer<String, Throwable> logFunction = e.getStatusCode().is4xxClientError() ? log::warn : log::error;
        logFunction.accept(e.getMessage(), e);
        return new ResponseEntity<>(new Error(e.getMessage()), e.getStatusCode());
    }


}