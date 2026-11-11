package com.angelbelzunce.ingresos_y_gastos.exception;

public class PaymentWithNotPendingSharesException extends RuntimeException {
    public PaymentWithNotPendingSharesException(Long paymentId) {
        super("The payment with id: " + paymentId + " has SETTLED or CONFIRMED shares");
    }
}
