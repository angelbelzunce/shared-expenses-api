package com.angelbelzunce.ingresos_y_gastos.dto;

import jakarta.validation.constraints.NotNull;

public record InvitationRequest(
        @NotNull
        Long groupId,
        @NotNull
        Long invitedUserId,
        @NotNull
        Long invitedById
) {
}
