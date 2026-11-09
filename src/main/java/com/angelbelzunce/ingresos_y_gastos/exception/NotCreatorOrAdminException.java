package com.angelbelzunce.ingresos_y_gastos.exception;

public class NotCreatorOrAdminException extends RuntimeException {
    public NotCreatorOrAdminException(Long id) {
        super("User with id " + id + " is not creator or ADMIN");
    }
}
