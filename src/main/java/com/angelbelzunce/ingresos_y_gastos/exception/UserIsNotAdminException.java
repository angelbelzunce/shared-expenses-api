package com.angelbelzunce.ingresos_y_gastos.exception;

public class UserIsNotAdminException extends RuntimeException {
    public UserIsNotAdminException(Long userId) {
        super("User with id: " + userId + " does not have ADMIN role");
    }
}
