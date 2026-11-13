package com.angelbelzunce.ingresos_y_gastos.exception;

public class CreatorCannotLeavePaymentException extends RuntimeException {
    public CreatorCannotLeavePaymentException() {
        super("Creator cannot leave their own payment");
    }
}
