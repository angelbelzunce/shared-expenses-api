package com.angelbelzunce.ingresos_y_gastos.exception;

public class UserIsAlreadyAdminException extends RuntimeException {
    public UserIsAlreadyAdminException(Long userId) {
        super("User with id: " + userId + " is already ADMIN");
    }
}
