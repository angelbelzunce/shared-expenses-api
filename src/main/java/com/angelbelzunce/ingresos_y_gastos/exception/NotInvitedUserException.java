package com.angelbelzunce.ingresos_y_gastos.exception;

public class NotInvitedUserException extends RuntimeException {
    public NotInvitedUserException(Long id) {
        super("This invitation is not for user with id: " + id);
    }
}
