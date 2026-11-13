package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.PaymentResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.*;
import com.angelbelzunce.ingresos_y_gastos.enums.PaymentStatus;
import com.angelbelzunce.ingresos_y_gastos.enums.ShareStatus;
import com.angelbelzunce.ingresos_y_gastos.enums.UserRole;
import com.angelbelzunce.ingresos_y_gastos.exception.*;
import com.angelbelzunce.ingresos_y_gastos.mapper.PaymentMapper;
import com.angelbelzunce.ingresos_y_gastos.repository.*;
import com.angelbelzunce.ingresos_y_gastos.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PaymentShareRepository shareRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponse findById(Long id) {
        return paymentMapper.toResponse(paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id)));
    }

    @Override
    public List<PaymentResponse> findPaymentsByGroup(Long groupId, Long userId, PaymentStatus status) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        List<Payment> payments = paymentRepository.findPaymentsByGroupAndUser(groupId, userId, status);


        return payments.stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponse create(PaymentRequest request, Long userId) {

        if (request.shareUsersId().contains(userId)) {
            throw new CreatorInShareUsersException();
        }

        Set<Long> shareUserSet = new HashSet<>(request.shareUsersId());

        if (request.shareUsersId().size() != shareUserSet.size()) {
            throw new DuplicatedShareUserException();
        }

        Payment payment = paymentMapper.toEntity(request);

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        Group group = groupRepository.findById(request.groupId()).orElseThrow(() -> new GroupNotFoundException(request.groupId()));

        if (!groupMemberRepository.existsByGroupIdAndUserId(request.groupId(), userId)) {
            throw new UserNotInGroupException(user.getId(), group.getId());
        }

        for (Long shareUserId : request.shareUsersId()) {
            if (!groupMemberRepository.existsByGroupIdAndUserId(request.groupId(), shareUserId)) {
                throw new UserNotInGroupException(shareUserId, group.getId());
            }
        }

        payment.setUser(user);
        payment.setGroup(group);

        if (request.shareUsersId().isEmpty()) {
            payment.setPaymentStatus(PaymentStatus.PAID);
        } else {
            payment.setPaymentStatus(PaymentStatus.PENDING);
        }

        Payment saved = paymentRepository.save(payment);

        int shareUsers = request.shareUsersId().size() + 1;
        BigDecimal shareAmount = request.amount().divide(BigDecimal.valueOf(shareUsers), 2, RoundingMode.HALF_UP);

        PaymentShare paymentShare;
        for (Long shareUserId : request.shareUsersId()) {
            paymentShare = new PaymentShare();
            paymentShare.setPayment(saved);
            paymentShare.setUser(userRepository.findById(shareUserId).orElseThrow(() -> new UserNotFoundException(shareUserId)));
            paymentShare.setAmount(shareAmount);
            paymentShare.setStatus(ShareStatus.PENDING);
            shareRepository.save(paymentShare);
        }

        PaymentShare paymentShareCreator = new PaymentShare();
        paymentShareCreator.setPayment(saved);
        paymentShareCreator.setUser(user);
        BigDecimal amountShareCreator = request.amount().subtract(shareAmount.multiply(BigDecimal.valueOf((shareUsers - 1))));
        paymentShareCreator.setAmount(amountShareCreator);
        paymentShareCreator.setStatus(ShareStatus.CONFIRMED);
        paymentShareCreator.setPaidAt(LocalDateTime.now());
        shareRepository.save(paymentShareCreator);

        return paymentMapper.toResponse(payment);

    }

    @Override
    @Transactional
    public PaymentResponse update(Long paymentId, Long userId, PaymentRequest request) {

        // Look for payment to update
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (request.shareUsersId().contains(payment.getUser().getId())) {
            throw new CreatorInShareUsersException();
        }

        Set<Long> shareUserSet = new HashSet<>(request.shareUsersId());

        if (request.shareUsersId().size() != shareUserSet.size()) {
            throw new DuplicatedShareUserException();
        }

        for (Long shareUserId : request.shareUsersId()) {
            if (!groupMemberRepository.existsByGroupIdAndUserId(request.groupId(), shareUserId)) {
                throw new UserNotInGroupException(shareUserId, request.groupId());
            }
        }

        // Check if updater is creator or ADMIN
        checkCanModify(payment, userId);

        List<PaymentShare> shares = shareRepository.findByPaymentId(payment.getId());

        shares.forEach(paymentShare -> {
            if (!paymentShare.getStatus().equals(ShareStatus.PENDING) && !paymentShare.getUser().getId().equals(userId))
                throw new PaymentWithNotPendingSharesException(payment.getId());
        });

        // Update payment
        payment.setTitle(request.title());
        payment.setDescription(request.description());
        payment.setAmount(request.amount());
        payment.setTransactionType(request.transactionType());

        if (request.shareUsersId().isEmpty()) {
            payment.setPaymentStatus(PaymentStatus.PAID);
        } else {
            payment.setPaymentStatus(PaymentStatus.PENDING);
        }

        Payment savedPayment = paymentRepository.save(payment);

        // Calculate new share amount
        int shareUsers = request.shareUsersId().size() + 1;
        BigDecimal shareAmount = request.amount().divide(BigDecimal.valueOf(shareUsers), 2, RoundingMode.HALF_UP);

        // Delete old payments share
        shareRepository.deleteByPaymentId(payment.getId());
        shareRepository.flush();

        // Create new payment shares from request
        PaymentShare paymentShare;
        for (Long shareUserId : request.shareUsersId()) {
            paymentShare = new PaymentShare();
            paymentShare.setPayment(savedPayment);
            paymentShare.setUser(userRepository.findById(shareUserId).orElseThrow(() -> new UserNotFoundException(shareUserId)));
            paymentShare.setAmount(shareAmount);
            paymentShare.setStatus(ShareStatus.PENDING);
            shareRepository.save(paymentShare);
        }

        PaymentShare paymentShareCreator = new PaymentShare();
        paymentShareCreator.setPayment(savedPayment);
        paymentShareCreator.setUser(payment.getUser());
        BigDecimal amountShareCreator = request.amount().subtract(shareAmount.multiply(BigDecimal.valueOf((shareUsers - 1))));
        paymentShareCreator.setAmount(amountShareCreator);
        paymentShareCreator.setStatus(ShareStatus.CONFIRMED);
        paymentShareCreator.setPaidAt(LocalDateTime.now());
        shareRepository.save(paymentShareCreator);

        // Save updated Payment and return response
        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    public void delete(Long paymentId, Long userId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        checkCanModify(payment, userId);

        List<PaymentShare> shares = shareRepository.findByPaymentId(paymentId);

        shares.forEach(paymentShare -> {
            if (!paymentShare.getStatus().equals(ShareStatus.PENDING) && !paymentShare.getUser().getId().equals(userId))
                throw new PaymentWithNotPendingSharesException(paymentId);
        });

        paymentRepository.delete(payment);

    }

    @Override
    @Transactional
    public void leave(Long paymentId, Long currentUserId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        Optional<PaymentShare> optionalShare = shareRepository.findByPaymentIdAndUserId(paymentId, currentUserId);

        if (optionalShare.isEmpty()) {
            throw new PaymentShareNotFoundException(paymentId, currentUserId);
        }

        if (payment.getUser().getId().equals(currentUserId)) {
            throw new CreatorCannotLeavePaymentException();
        }

        List<PaymentShare> shareList = shareRepository.findByPaymentId(paymentId);

        for (PaymentShare share : shareList) {
            if (!share.getUser().getId().equals(payment.getUser().getId())
                    && !share.getStatus().equals(ShareStatus.PENDING)) {

                throw new PaymentWithNotPendingSharesException(paymentId);
            }
        }

        shareList.removeIf(share -> share.getUser().getId().equals(currentUserId));
        shareRepository.delete(optionalShare.get());

        // Calculate new share amount
        int shareUsers = shareList.size();
        BigDecimal shareAmount = payment.getAmount().divide(BigDecimal.valueOf(shareUsers), 2, RoundingMode.HALF_UP);

        BigDecimal amountShareCreator = payment.getAmount().subtract(shareAmount.multiply(BigDecimal.valueOf((shareUsers - 1))));

        shareList.forEach(share ->
        {
            share.setAmount(shareAmount);
            if (share.getUser().getId().equals(payment.getUser().getId())) share.setAmount(amountShareCreator);

        });

        if (shareList.size() == 1) {
            payment.setPaymentStatus(PaymentStatus.PAID);
        }


    }

    private void checkCanModify(Payment payment, Long userId) {

        boolean isPaymentCreator = userId.equals(payment.getUser().getId());

        Optional<GroupMember> optionalMember = groupMemberRepository.findByGroupIdAndUserId(payment.getGroup().getId(), userId);

        if (optionalMember.isEmpty()) throw new UserNotInGroupException(userId, payment.getGroup().getId());

        boolean isAdmin = optionalMember.get().getUserRole().equals(UserRole.ADMIN);

        if (!isPaymentCreator && !isAdmin) {
            throw new NotCreatorOrAdminException(userId);

        }
    }
}
