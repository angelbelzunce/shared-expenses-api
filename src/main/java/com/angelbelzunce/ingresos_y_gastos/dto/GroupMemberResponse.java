package com.angelbelzunce.ingresos_y_gastos.dto;

import com.angelbelzunce.ingresos_y_gastos.enums.Role;

import java.time.LocalDateTime;

public record GroupMemberResponse(
        Long id,
        String name,
        Role role,
        LocalDateTime joinedAt
) {
}
