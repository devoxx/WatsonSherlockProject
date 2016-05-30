package com.devoxx.watson.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Stephan Janssen
 */
@ControllerAdvice
public class FileUploadErrorController {

    /**
     * TODO Show a HTML page instead.
     *
     * @param request   the https request
     * @param ex    the exception
     * @return a JSON file with error details
     */
    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    ResponseEntity<?> handleControllerException(HttpServletRequest request, Throwable ex) {

        final HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new CustomErrorType(internalServerError.value(), ex.getCause().toString()), internalServerError);
    }
}

class CustomErrorType {

    private int value;
    private String msg;

    CustomErrorType(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public int getValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }
}

