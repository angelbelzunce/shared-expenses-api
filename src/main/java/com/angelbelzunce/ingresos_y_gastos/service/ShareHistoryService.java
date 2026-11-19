package com.angelbelzunce.ingresos_y_gastos.service;

import com.angelbelzunce.ingresos_y_gastos.dto.ShareHistoryResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.PaymentShare;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.enums.ShareStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ShareHistoryService {

    void create(PaymentShare currentShare, User currentUser, ShareStatus fromStatus, ShareStatus toStatus);

    List<ShareHistoryResponse> findHistoryByPayment(Long paymentId, Long currentUserId);
}
