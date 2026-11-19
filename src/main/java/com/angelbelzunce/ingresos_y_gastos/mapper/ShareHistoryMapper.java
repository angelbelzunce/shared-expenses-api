package com.angelbelzunce.ingresos_y_gastos.mapper;

import com.angelbelzunce.ingresos_y_gastos.dto.ShareHistoryResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.ShareHistory;
import org.springframework.stereotype.Component;

@Component
public class ShareHistoryMapper {

   public ShareHistoryResponse toResponse(ShareHistory shareHistory) {
        return new ShareHistoryResponse(
                shareHistory.getActingUser().getId(),
                shareHistory.getActingUser().getName(),
                shareHistory.getShare().getUser().getId(),
                shareHistory.getShare().getUser().getName(),
                shareHistory.getFromStatus(),
                shareHistory.getToStatus(),
                shareHistory.getModifiedAt()
        );
    }
}
