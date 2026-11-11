package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentShareResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.PaymentShare;
import com.angelbelzunce.ingresos_y_gastos.enums.ShareStatus;
import com.angelbelzunce.ingresos_y_gastos.exception.PaymentNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.exception.PaymentShareNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.exception.UserNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.mapper.PaymentShareMapper;
import com.angelbelzunce.ingresos_y_gastos.repository.PaymentRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.PaymentShareRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.UserRepository;
import com.angelbelzunce.ingresos_y_gastos.service.PaymentShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    public PaymentShareResponse settle(Long paymentId, Long userId) {

        PaymentShare paymentShare = paymentShareRepository.findByPaymentIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new PaymentShareNotFoundException(paymentId, userId));

        if (!paymentShare.getStatus().equals(ShareStatus.PENDING)){
            throw new RuntimeException(); //TODO: Create Exception
        }
        paymentShare.setStatus(ShareStatus.SETTLED);
        paymentShare.setPaidAt(LocalDateTime.now());

        return paymentShareMapper.toResponse(paymentShareRepository.save(paymentShare));
    }
}
