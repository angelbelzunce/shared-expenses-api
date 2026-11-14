package com.angelbelzunce.ingresos_y_gastos.dto;

import com.angelbelzunce.ingresos_y_gastos.enums.PaymentStatus;
import com.angelbelzunce.ingresos_y_gastos.enums.ShareStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserShareResponse(
        Long shareId,
        Long paymentId,
        String paymentTitle,
        Long groupId,
        String groupName,
        BigDecimal amount,
        ShareStatus status,
        LocalDateTime paidAt
        ) {
}
