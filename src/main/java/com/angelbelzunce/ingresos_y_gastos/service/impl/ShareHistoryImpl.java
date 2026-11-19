package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.ShareHistoryResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.PaymentShare;
import com.angelbelzunce.ingresos_y_gastos.entity.ShareHistory;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.enums.ShareStatus;
import com.angelbelzunce.ingresos_y_gastos.exception.PaymentNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.exception.UserNotInPaymentException;
import com.angelbelzunce.ingresos_y_gastos.mapper.ShareHistoryMapper;
import com.angelbelzunce.ingresos_y_gastos.repository.PaymentRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.PaymentShareRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.ShareHistoryRepository;
import com.angelbelzunce.ingresos_y_gastos.service.ShareHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShareHistoryImpl implements ShareHistoryService {

    private final ShareHistoryRepository historyRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentShareRepository shareRepository;
    private final ShareHistoryMapper historyMapper;

    @Override
    public void create(PaymentShare currentShare, User currentUser, ShareStatus fromStatus, ShareStatus toStatus) {
        ShareHistory shareHistory = new ShareHistory();
        shareHistory.setShare(currentShare);
        shareHistory.setActingUser(currentUser);
        shareHistory.setFromStatus(fromStatus);
        shareHistory.setToStatus(toStatus);

        historyRepository.save(shareHistory);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ShareHistoryResponse> findHistoryByPayment(Long paymentId, Long currentUserId) {

        paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (!shareRepository.existsByPaymentIdAndUserId(paymentId, currentUserId)){
            throw new UserNotInPaymentException(currentUserId,paymentId);
        }

         return historyRepository.findByShare_Payment_IdOrderByModifiedAt(paymentId)
                .stream()
                .map(historyMapper::toResponse)
                .toList();
    }
}
