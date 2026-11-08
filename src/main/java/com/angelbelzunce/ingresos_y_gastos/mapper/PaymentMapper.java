package com.angelbelzunce.ingresos_y_gastos.mapper;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.PaymentResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequest paymentRequest){
        Payment payment = new Payment();
        payment.setTitle(paymentRequest.title());
        payment.setDescription(paymentRequest.description());
        payment.setAmount(paymentRequest.amount());
        payment.setPaymentStatus(paymentRequest.paymentStatus());
        payment.setTransactionType(paymentRequest.transactionType());

        return payment;
    }

    public PaymentResponse toResponse(Payment payment){
        return new PaymentResponse(
                payment.getId(),
                payment.getTitle(),
                payment.getDescription(),
                payment.getAmount(),
                payment.getCreatedAt(),
                payment.getPaymentStatus(),
                payment.getTransactionType(),
                payment.getUser().getId(),
                payment.getGroup().getId());
    }
}
