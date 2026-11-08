package com.angelbelzunce.ingresos_y_gastos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GroupRequest(
        @NotBlank
        @Size(max = 50)
        String name,
        // TODO: extract user from authentication
        @NotNull
        Long userId
) {


}
