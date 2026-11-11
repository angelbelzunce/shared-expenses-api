package com.angelbelzunce.ingresos_y_gastos.exception;

public class UserCannotUndoTheirShare extends RuntimeException {
    public UserCannotUndoTheirShare(Long userId) {
        super("The creator user with id: " + userId + " cannot undo their own confirmed share");
    }
}
