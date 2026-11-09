package com.angelbelzunce.ingresos_y_gastos.exception;

public class InvitationAlreadyExistsException extends RuntimeException {
    public InvitationAlreadyExistsException() {
        super("Invited user already has a pending invitation for this group");
    }
}
