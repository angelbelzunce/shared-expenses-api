package com.angelbelzunce.ingresos_y_gastos.exception;

public class InvitationNotFoundException extends RuntimeException {
    public InvitationNotFoundException(Long id) {
        super("Invitation not found with id: " + id);
    }
}
