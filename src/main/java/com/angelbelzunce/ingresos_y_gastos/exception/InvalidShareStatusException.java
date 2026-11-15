package com.angelbelzunce.ingresos_y_gastos.exception;

import com.angelbelzunce.ingresos_y_gastos.enums.ShareStatus;

public class InvalidShareStatusException extends RuntimeException {
    public InvalidShareStatusException(Long shareId, ShareStatus expectedStatus) {
        super("The share with id: " + shareId + " is not " + expectedStatus);
    }
}
