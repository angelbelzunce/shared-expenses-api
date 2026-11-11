package com.angelbelzunce.ingresos_y_gastos.exception;

public class IsNotShareOwnerException extends RuntimeException {
    public IsNotShareOwnerException(Long id) {
        super("User with id: " + id + " is not owner of this payment share");
    }
}
