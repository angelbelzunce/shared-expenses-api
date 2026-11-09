package com.angelbelzunce.ingresos_y_gastos.exception;

public class InvitationNotPendingException extends RuntimeException {
    public InvitationNotPendingException(Long id) {
        super("Invitation not pending with id: " + id);
    }
}
