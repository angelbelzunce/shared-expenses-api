package com.angelbelzunce.ingresos_y_gastos.exception;

public class NotPendingShareException extends RuntimeException {
    public NotPendingShareException(Long shareId) {
        super("The share with id: " + shareId + " is not PENDING");
    }
}
