package com.miaoqichao.common.exception;

import lombok.Getter;

/**
 * Custom Business Exception
 */
@Getter
public class BusinessException extends RuntimeException {
    private final long code;
    private final String message;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public BusinessException(long code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}