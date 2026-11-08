package com.angelbelzunce.ingresos_y_gastos.dto;

import com.angelbelzunce.ingresos_y_gastos.enums.Status;
import com.angelbelzunce.ingresos_y_gastos.enums.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank
        @Size(max = 30)
        String title,
        @Size(max = 255)
        String description,
        @NotNull
        @Positive
        @Digits(integer = 36, fraction = 2)
        BigDecimal amount,
        @NotNull
        Status status,
        @NotNull
        TransactionType transactionType,
        // TODO: take user from authentication
        @NotNull
        Long userId,
        @NotNull
        Long groupId

) {
}
