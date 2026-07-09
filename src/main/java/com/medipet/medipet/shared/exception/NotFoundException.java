package com.medipet.medipet.shared.exception;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super("NOT_FOUND", message);
    }

    public NotFoundException(String entity, Object id) {
        super("NOT_FOUND", "%s with id %s not found".formatted(entity, id));
    }
}
