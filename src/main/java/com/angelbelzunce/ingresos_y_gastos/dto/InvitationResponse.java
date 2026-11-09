package com.angelbelzunce.ingresos_y_gastos.dto;

import com.angelbelzunce.ingresos_y_gastos.enums.InvitationStatus;

import java.time.LocalDateTime;

public record InvitationResponse(
        Long id,
        Long groupId,
        Long invitedUserId,
        Long invitedById,
        InvitationStatus invitationStatus,
        LocalDateTime createdAt
) {
}
