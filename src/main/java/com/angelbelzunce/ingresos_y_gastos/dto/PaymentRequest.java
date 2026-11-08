package com.angelbelzunce.ingresos_y_gastos.dto;

import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.enums.Status;
import com.angelbelzunce.ingresos_y_gastos.enums.TransactionType;

import java.math.BigDecimal;

public record PaymentRequest(
        String title,
        String description,
        BigDecimal amount,
        Status status,
        TransactionType transactionType,
        // TODO: take user from authentication
        Long userId,
        Long groupId

) {
}
