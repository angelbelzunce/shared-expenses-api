package com.angelbelzunce.ingresos_y_gastos.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ShareRequest(
        @NotNull
        Long id,
        @Positive
        @Digits(integer = 36, fraction = 2)
        BigDecimal amount
) {
}
