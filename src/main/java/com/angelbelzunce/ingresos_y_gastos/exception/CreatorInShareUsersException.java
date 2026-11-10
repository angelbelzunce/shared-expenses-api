package com.angelbelzunce.ingresos_y_gastos.exception;

public class CreatorInShareUsersException extends RuntimeException {
    public CreatorInShareUsersException() {
        super("The creator cannot be included in share users");
    }
}
