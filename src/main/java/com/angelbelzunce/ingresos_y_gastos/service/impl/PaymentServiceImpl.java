package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.PaymentResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.Group;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.exception.GroupNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.exception.PaymentNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.exception.UserNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.exception.UserNotInGroupException;
import com.angelbelzunce.ingresos_y_gastos.mapper.PaymentMapper;
import com.angelbelzunce.ingresos_y_gastos.entity.Payment;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupMemberRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.PaymentRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.UserRepository;
import com.angelbelzunce.ingresos_y_gastos.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PaymentMapper paymentMapper;


    @Override
    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse findById(Long id) {
        return paymentMapper.toResponse(paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id)));
    }

    @Override
    public PaymentResponse create(PaymentRequest request) {
        Payment payment = paymentMapper.toEntity(request);
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new UserNotFoundException(request.userId()));
        Group group = groupRepository.findById(request.groupId()).orElseThrow(() -> new GroupNotFoundException(request.groupId()));
        if (!groupMemberRepository.existsByGroupIdAndUserId(request.groupId(), request.userId())){
            throw new UserNotInGroupException(user.getId(), group.getId());
        }

        payment.setUser(user);
        payment.setGroup(group);
        return paymentMapper.toResponse(paymentRepository.save(payment));

    }

    @Override
    public PaymentResponse update(Long id, PaymentRequest request) {
        // Look for payment to update
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        // Update payment
        payment.setTitle(request.title());
        payment.setDescription(request.description());
        payment.setAmount(request.amount());
        payment.setStatus(request.status());
        payment.setTransactionType(request.transactionType());

        // Save updated Payment and return response
        return paymentMapper.toResponse(paymentRepository.save(payment));

    }

    @Override
    public void delete(Long id) {
        Payment payment = paymentRepository.findById(id)
                        .orElseThrow(() -> new PaymentNotFoundException(id));

        paymentRepository.delete(payment);

    }
}
