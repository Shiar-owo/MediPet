package com.medipet.medipet.shared.exception;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super("NOT_FOUND", message);
    }

    public NotFoundException(String entity, Long id) {
        super("NOT_FOUND", "%s with id %d not found".formatted(entity, id));
    }
}
