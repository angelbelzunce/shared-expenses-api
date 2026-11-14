package com.angelbelzunce.ingresos_y_gastos.mapper;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentShareResponse;
import com.angelbelzunce.ingresos_y_gastos.dto.UserShareResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.PaymentShare;
import org.springframework.stereotype.Component;

@Component
public class PaymentShareMapper {

    public PaymentShareResponse toResponse(PaymentShare paymentShare) {
        return new PaymentShareResponse(
                paymentShare.getId(),
                paymentShare.getPayment().getId(),
                paymentShare.getUser().getId(),
                paymentShare.getAmount(),
                paymentShare.getStatus(),
                paymentShare.getPaidAt());
    }

    public UserShareResponse toUserSharesResponse(PaymentShare paymentShare) {
        return new UserShareResponse(
                paymentShare.getId(),
                paymentShare.getPayment().getId(),
                paymentShare.getPayment().getTitle(),
                paymentShare.getPayment().getGroup().getId(),
                paymentShare.getPayment().getGroup().getName(),
                paymentShare.getAmount(),
                paymentShare.getStatus(),
                paymentShare.getPaidAt()
        );
    }


}
