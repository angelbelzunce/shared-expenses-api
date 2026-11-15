package com.angelbelzunce.ingresos_y_gastos.exception;

public class IsCreatorOrAdminException extends RuntimeException {
    public IsCreatorOrAdminException(Long id) {
        super("User with id " + id + " is the creator or ADMIN");
    }
}
