package com.angelbelzunce.ingresos_y_gastos.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentShareRequest(
        @NotNull
        Long paymentId,
        @NotNull
        Long userId // TODO: Get id from authentication

) {
}
