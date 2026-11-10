package com.angelbelzunce.ingresos_y_gastos.service;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentShareResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.PaymentShare;

import java.util.List;

public interface PaymentShareService {

    List<PaymentShareResponse> findByPaymentId(Long paymentId);

    PaymentShareResponse findByPaymentIdAndUserId(Long paymentId, Long userId);

    void deleteByPaymentId(Long paymentId);
}
