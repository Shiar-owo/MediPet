package com.medipet.medipet.shared.exception;

public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {
        super("BAD_REQUEST", message);
    }
}
