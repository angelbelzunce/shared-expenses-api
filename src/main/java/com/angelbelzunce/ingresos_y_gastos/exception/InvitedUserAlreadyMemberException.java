package com.angelbelzunce.ingresos_y_gastos.exception;

public class InvitedUserAlreadyMemberException extends RuntimeException {
    public InvitedUserAlreadyMemberException(Long groupId) {
        super("Invited user is already a member of group with id: " + groupId);
    }
}
