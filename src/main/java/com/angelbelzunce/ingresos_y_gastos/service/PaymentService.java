package com.angelbelzunce.ingresos_y_gastos.service;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.PaymentResponse;
import com.angelbelzunce.ingresos_y_gastos.enums.PaymentStatus;

import java.util.List;

public interface PaymentService {

    List<PaymentResponse> findAll();
    PaymentResponse findById(Long id);
    List<PaymentResponse> findByGroup(Long groupId, PaymentStatus paymentStatus);
    PaymentResponse create(PaymentRequest request);
    PaymentResponse update(Long id, Long userId, PaymentRequest request);
    void delete(Long id);

}
