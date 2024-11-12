package com.sicei.app.sicei.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BusinessException extends RuntimeException {
    private String code;
    private HttpStatus httpStatus;

    public BusinessException(String message, String code, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
