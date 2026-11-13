package com.angelbelzunce.ingresos_y_gastos.exception;

public class AdminCannotLeaveGroupException extends RuntimeException {
    public AdminCannotLeaveGroupException() {
        super("The ADMIN cannot leave the group (need to assign ADMIN to other member before)");
    }
}
