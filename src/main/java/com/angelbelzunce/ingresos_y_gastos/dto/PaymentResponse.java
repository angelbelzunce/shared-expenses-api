package com.angelbelzunce.ingresos_y_gastos.dto;

import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.enums.Status;
import com.angelbelzunce.ingresos_y_gastos.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        String title,
        String description,
        BigDecimal amount,
        LocalDateTime createdAt,
        Status status,
        TransactionType transactionType,
        Long userId,
        Long groupId
) {
}
