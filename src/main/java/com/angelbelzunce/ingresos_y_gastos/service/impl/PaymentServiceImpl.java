package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.PaymentResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.Group;
import com.angelbelzunce.ingresos_y_gastos.entity.GroupMember;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.enums.PaymentStatus;
import com.angelbelzunce.ingresos_y_gastos.enums.UserRole;
import com.angelbelzunce.ingresos_y_gastos.exception.*;
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
import java.util.Optional;

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
    public List<PaymentResponse> findByGroup(Long groupId, PaymentStatus paymentStatus) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        List<Payment> payments;

        if (paymentStatus == null) {
            payments = paymentRepository.findByGroupId(groupId);

        } else {
            payments = paymentRepository.findByGroupIdAndPaymentStatus(groupId, paymentStatus);

        }

        return payments.stream()
                .map(paymentMapper::toResponse)
                .toList();
    }


    @Override
    public PaymentResponse create(PaymentRequest request) {
        Payment payment = paymentMapper.toEntity(request);
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new UserNotFoundException(request.userId()));
        Group group = groupRepository.findById(request.groupId()).orElseThrow(() -> new GroupNotFoundException(request.groupId()));
        if (!groupMemberRepository.existsByGroupIdAndUserId(request.groupId(), request.userId())) {
            throw new UserNotInGroupException(user.getId(), group.getId());
        }

        payment.setUser(user);
        payment.setGroup(group);
        return paymentMapper.toResponse(paymentRepository.save(payment));

    }

    @Override
    public PaymentResponse update(Long id, Long userId, PaymentRequest request) {

        // Look for payment to update
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        checkCanModify(payment, userId);

        // Update payment
        payment.setTitle(request.title());
        payment.setDescription(request.description());
        payment.setAmount(request.amount());
        payment.setPaymentStatus(request.paymentStatus());
        payment.setTransactionType(request.transactionType());

        // Save updated Payment and return response
        return paymentMapper.toResponse(paymentRepository.save(payment));

    }

    @Override
    public void delete(Long id, Long userId) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        checkCanModify(payment, userId);

        paymentRepository.delete(payment);

    }

    private void checkCanModify(Payment payment, Long userId ){
        boolean isPaymentCreator = userId.equals(payment.getUser().getId());

        Optional<GroupMember> optionalMember = groupMemberRepository.findByGroupIdAndUserId(payment.getGroup().getId(), userId);
        if (optionalMember.isEmpty()) throw new UserNotInGroupException(userId, payment.getGroup().getId());
        boolean isAdmin = optionalMember.get().getUserRole().equals(UserRole.ADMIN);

        if (!isPaymentCreator && !isAdmin){
            throw new NotCreatorOrAdminException(userId);

        }
    }
}
