package com.angelbelzunce.ingresos_y_gastos.exception;

public class IsNotShareCreatorException extends RuntimeException {
    public IsNotShareCreatorException(Long id) {
        super("User with id: " + id + " is not creator of this INCOME");
    }
}
