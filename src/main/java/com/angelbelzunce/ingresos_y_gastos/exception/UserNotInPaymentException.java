package com.angelbelzunce.ingresos_y_gastos.exception;

public class UserNotInPaymentException extends RuntimeException {
    public UserNotInPaymentException(Long userId, Long paymentId) {
        super("User with id " + userId + " does not take part in payment with id " + paymentId);
    }
}
