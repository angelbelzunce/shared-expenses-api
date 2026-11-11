package com.angelbelzunce.ingresos_y_gastos.service;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentShareResponse;

import java.util.List;

public interface PaymentShareService {

    List<PaymentShareResponse> findByPaymentId(Long paymentId);

    PaymentShareResponse findByPaymentIdAndUserId(Long paymentId, Long userId);

    PaymentShareResponse settle(Long paymentId, Long shareUserId, Long userId);

    PaymentShareResponse confirmOrRejectSettle(Long paymentId, Long shareUserId, Long userId, boolean isConfirming);

}
