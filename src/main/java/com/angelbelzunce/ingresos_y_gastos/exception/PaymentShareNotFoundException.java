package com.angelbelzunce.ingresos_y_gastos.exception;

public class PaymentShareNotFoundException extends RuntimeException {
    public PaymentShareNotFoundException(Long paymentId, Long userId) {
        super("Payment share not found for payment with id: " + paymentId + " and user with id: " + userId);
    }
}
