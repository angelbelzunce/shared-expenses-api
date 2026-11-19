package com.angelbelzunce.ingresos_y_gastos.dto;

import com.angelbelzunce.ingresos_y_gastos.enums.ShareStatus;

import java.time.LocalDateTime;

public record ShareHistoryResponse(
        Long actingUserId,
        String actingUserName,
        Long shareOwnerId,
        String shareOwnerName,
        ShareStatus fromStatus,
        ShareStatus toStatus,
        LocalDateTime modifiedAt
) {
}
