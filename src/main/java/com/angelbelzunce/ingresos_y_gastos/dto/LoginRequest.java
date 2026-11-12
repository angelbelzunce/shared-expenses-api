package com.angelbelzunce.ingresos_y_gastos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}
