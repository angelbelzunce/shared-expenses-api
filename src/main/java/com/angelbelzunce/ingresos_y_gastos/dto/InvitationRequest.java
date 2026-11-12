package com.angelbelzunce.ingresos_y_gastos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InvitationRequest(
        @NotNull
        Long groupId,
        @NotBlank
        @Email
        String invitedUserEmail
) {
}
