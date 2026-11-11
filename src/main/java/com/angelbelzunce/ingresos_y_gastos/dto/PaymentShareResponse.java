package com.angelbelzunce.ingresos_y_gastos.dto;

import com.angelbelzunce.ingresos_y_gastos.enums.ShareStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentShareResponse(
        Long id,
        Long paymentId,
        Long userId,
        BigDecimal amount,
        ShareStatus status,
        LocalDateTime paidAt) {
}
