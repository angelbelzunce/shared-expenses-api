package com.angelbelzunce.ingresos_y_gastos.service;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {

    List<PaymentResponse> findAll();
    PaymentResponse findById(Long id);
    PaymentResponse create(PaymentRequest request);
    PaymentResponse update(Long id, PaymentRequest request);
    void delete(Long id);

}
