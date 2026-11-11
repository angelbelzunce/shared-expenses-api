package com.angelbelzunce.ingresos_y_gastos.exception;

public class DuplicatedShareUserException extends RuntimeException {
    public DuplicatedShareUserException() {
        super("Duplicate user in payment share");
    }
}
