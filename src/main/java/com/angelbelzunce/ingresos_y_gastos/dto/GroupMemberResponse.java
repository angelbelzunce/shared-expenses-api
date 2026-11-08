package com.angelbelzunce.ingresos_y_gastos.dto;

import com.angelbelzunce.ingresos_y_gastos.enums.UserRole;

import java.time.LocalDateTime;

public record GroupMemberResponse(
        Long id,
        String name,
        UserRole userRole,
        LocalDateTime joinedAt
) {
}
