package com.angelbelzunce.ingresos_y_gastos.exception;

public class UserIsNotAdminException extends RuntimeException {
    public UserIsNotAdminException() {
        super("User does not have ADMIN role");
    }
}
