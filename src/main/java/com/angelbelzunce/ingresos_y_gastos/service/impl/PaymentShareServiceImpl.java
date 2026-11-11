package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentShareResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.Payment;
import com.angelbelzunce.ingresos_y_gastos.entity.PaymentShare;
import com.angelbelzunce.ingresos_y_gastos.enums.ShareStatus;
import com.angelbelzunce.ingresos_y_gastos.enums.TransactionType;
import com.angelbelzunce.ingresos_y_gastos.exception.*;
import com.angelbelzunce.ingresos_y_gastos.mapper.PaymentShareMapper;
import com.angelbelzunce.ingresos_y_gastos.repository.PaymentRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.PaymentShareRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.UserRepository;
import com.angelbelzunce.ingresos_y_gastos.service.PaymentShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class PaymentShareServiceImpl implements PaymentShareService {

    private final PaymentShareRepository paymentShareRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentShareMapper paymentShareMapper;

    @Override
    public List<PaymentShareResponse> findByPaymentId(Long paymentId) {

        paymentRepository.findById(paymentId).orElseThrow(() -> new PaymentNotFoundException(paymentId));

        return paymentShareRepository.findByPaymentId(paymentId)
                .stream()
                .map(paymentShareMapper::toResponse).toList();
    }

    @Override
    public PaymentShareResponse findByPaymentIdAndUserId(Long paymentId, Long userId) {
        paymentRepository.findById(paymentId).orElseThrow(() -> new PaymentNotFoundException(paymentId));
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        return paymentShareMapper.toResponse(paymentShareRepository.findByPaymentIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new PaymentShareNotFoundException(paymentId, userId)));
    }

    @Override
    public PaymentShareResponse settle(Long paymentId, Long shareUserId, Long userId) {

        PaymentShare paymentShare = paymentShareRepository.findByPaymentIdAndUserId(paymentId, shareUserId)
                .orElseThrow(() -> new PaymentShareNotFoundException(paymentId, shareUserId));

        // If share has not PENDING status, cannot settle it
        if (!paymentShare.getStatus().equals(ShareStatus.PENDING)) {
            throw new NotPendingShareException(paymentShare.getId());
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));


        boolean isIncome = payment.getTransactionType().equals(TransactionType.INCOME);
        boolean isCreator = payment.getUser().getId().equals(userId);
        boolean isShareOfUser = paymentShare.getUser().getId().equals(userId);

        // If payment is an income but authenticated user is not the payment creator
        if (isIncome && !isCreator) {
            throw new IsNotShareCreatorException(userId);
        }

        // If payment is an expense but authenticated user is not the share owner
        if (!isIncome && !isShareOfUser) {
            throw new IsNotShareOwnerException(userId);
        }


        paymentShare.setStatus(ShareStatus.SETTLED);
        paymentShare.setPaidAt(LocalDateTime.now());

        return paymentShareMapper.toResponse(paymentShareRepository.save(paymentShare));
    }

    @Override
    public PaymentShareResponse confirm(Long paymentId, Long shareUserId, Long userId) {

        PaymentShare paymentShare = paymentShareRepository.findByPaymentIdAndUserId(paymentId, shareUserId)
                .orElseThrow(() -> new PaymentShareNotFoundException(paymentId, shareUserId));

        // If share has not PENDING status, cannot confirm it
        if (!paymentShare.getStatus().equals(ShareStatus.SETTLED)) {
            throw new NotPendingShareException(paymentShare.getId());
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));


        boolean isIncome = payment.getTransactionType().equals(TransactionType.INCOME);
        boolean isPaymentCreator = payment.getUser().getId().equals(userId);
        boolean isShareOfUser = paymentShare.getUser().getId().equals(userId);

        // If payment is an income but authenticated user is not the payment creator
        if (!isIncome && !isPaymentCreator) {
            throw new IsNotShareCreatorException(userId);
        }

        // If payment is an expense but authenticated user is not the share owner
        if (isIncome && !isShareOfUser) {
            throw new IsNotShareOwnerException(userId);
        }

        paymentShare.setStatus(ShareStatus.CONFIRMED);

        return paymentShareMapper.toResponse(paymentShareRepository.save(paymentShare));

    }
}
