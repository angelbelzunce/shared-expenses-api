package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.PaymentResponse;
import com.angelbelzunce.ingresos_y_gastos.dto.ShareRequest;
import com.angelbelzunce.ingresos_y_gastos.entity.*;
import com.angelbelzunce.ingresos_y_gastos.enums.PaymentStatus;
import com.angelbelzunce.ingresos_y_gastos.enums.ShareStatus;
import com.angelbelzunce.ingresos_y_gastos.enums.ShareType;
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

        validateShares(request, userId);

        Payment payment = paymentMapper.toEntity(request);

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        Group group = groupRepository.findById(request.groupId()).orElseThrow(() -> new GroupNotFoundException(request.groupId()));

        if (!groupMemberRepository.existsByGroupIdAndUserId(request.groupId(), userId)) {
            throw new UserNotInGroupException(user.getId(), group.getId());
        }

        for (ShareRequest shareRequest : request.shares()) {
            if (!groupMemberRepository.existsByGroupIdAndUserId(request.groupId(), shareRequest.id())) {
                throw new UserNotInGroupException(shareRequest.id(), group.getId());
            }
        }

        payment.setUser(user);
        payment.setGroup(group);

        if (request.shares().isEmpty()) {
            payment.setPaymentStatus(PaymentStatus.PAID);
        } else {
            payment.setPaymentStatus(PaymentStatus.PENDING);
        }

        Payment saved = paymentRepository.save(payment);

        createShares(saved, user, request);

        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse update(Long paymentId, Long userId, PaymentRequest request) {

        // Look for payment to update
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        validateShares(request, payment.getUser().getId());

        for (ShareRequest shareRequest : request.shares()) {
            if (!groupMemberRepository.existsByGroupIdAndUserId(request.groupId(), shareRequest.id())) {
                throw new UserNotInGroupException(shareRequest.id(), request.groupId());
            }
        }

        // Check if updater is creator or ADMIN
        checkCanModify(payment, userId);

        List<PaymentShare> shares = shareRepository.findByPaymentId(payment.getId());

        shares.forEach(paymentShare -> {
            if (!paymentShare.getStatus().equals(ShareStatus.PENDING) && !paymentShare.getUser().getId().equals(payment.getUser().getId()))
                throw new PaymentWithNotPendingSharesException(payment.getId());
        });

        // Update payment
        payment.setTitle(request.title());
        payment.setDescription(request.description());
        payment.setAmount(request.amount());
        payment.setTransactionType(request.transactionType());
        payment.setShareType(request.shareType());

        if (request.shares().isEmpty()) {
            payment.setPaymentStatus(PaymentStatus.PAID);
        } else {
            payment.setPaymentStatus(PaymentStatus.PENDING);
        }

        Payment savedPayment = paymentRepository.save(payment);

        // Delete old payments share
        shareRepository.deleteByPaymentId(payment.getId());
        shareRepository.flush();

        // Create new payment shares from request
        createShares(savedPayment, payment.getUser(), request);

        // Save updated Payment and return response
        return paymentMapper.toResponse(savedPayment);
    }

    // Checks the share users and, in AMOUNTS mode, their amounts
    private void validateShares(PaymentRequest request, Long creatorId) {

        Set<Long> shareUserIds = new HashSet<>();

        for (ShareRequest shareRequest : request.shares()) {
            if (shareRequest.id().equals(creatorId)) {
                throw new CreatorInShareUsersException();
            }
            if (!shareUserIds.add(shareRequest.id())) {
                throw new DuplicatedShareUserException();
            }
        }

        if (request.shareType().equals(ShareType.AMOUNTS)) {

            BigDecimal sharesTotal = BigDecimal.ZERO;

            for (ShareRequest shareRequest : request.shares()) {
                if (shareRequest.amount() == null) {
                    throw new InvalidShareAmountsException("Every share needs an amount when the share type is AMOUNTS");
                }
                sharesTotal = sharesTotal.add(shareRequest.amount());
            }

            if (sharesTotal.compareTo(request.amount()) > 0) {
                throw new InvalidShareAmountsException("The shares add up to " + sharesTotal + ", more than the payment amount " + request.amount());
            }
        }
    }

    // Creates one PENDING share per share user and the creator's CONFIRMED share with the remainder
    private void createShares(Payment payment, User creator, PaymentRequest request) {

        boolean isEquals = request.shareType().equals(ShareType.EQUALS);

        int shareUsers = request.shares().size() + 1;
        BigDecimal equalsShareAmount = request.amount().divide(BigDecimal.valueOf(shareUsers), 2, RoundingMode.HALF_UP);

        BigDecimal sharesTotal = BigDecimal.ZERO;

        PaymentShare paymentShare;
        for (ShareRequest shareRequest : request.shares()) {
            BigDecimal amount = isEquals ? equalsShareAmount : shareRequest.amount();

            paymentShare = new PaymentShare();
            paymentShare.setPayment(payment);
            paymentShare.setUser(userRepository.findById(shareRequest.id()).orElseThrow(() -> new UserNotFoundException(shareRequest.id())));
            paymentShare.setAmount(amount);
            paymentShare.setStatus(ShareStatus.PENDING);
            shareRepository.save(paymentShare);

            sharesTotal = sharesTotal.add(amount);
        }

        // The creator keeps the remainder: rounding cents in EQUALS mode, the rest of the amount in AMOUNTS mode
        PaymentShare paymentShareCreator = new PaymentShare();
        paymentShareCreator.setPayment(payment);
        paymentShareCreator.setUser(creator);
        paymentShareCreator.setAmount(request.amount().subtract(sharesTotal));
        paymentShareCreator.setStatus(ShareStatus.CONFIRMED);
        paymentShareCreator.setPaidAt(LocalDateTime.now());
        shareRepository.save(paymentShareCreator);
    }

    @Override
    public void delete(Long paymentId, Long userId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        checkCanModify(payment, userId);

        List<PaymentShare> shares = shareRepository.findByPaymentId(paymentId);

        shares.forEach(paymentShare -> {
            if (!paymentShare.getStatus().equals(ShareStatus.PENDING) && !paymentShare.getUser().getId().equals(payment.getUser().getId()))
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

        if (payment.getShareType().equals(ShareType.AMOUNTS)) {

            // Exact amounts: the others keep theirs and the leaver's amount goes to the creator
            shareList.forEach(share -> {
                if (share.getUser().getId().equals(payment.getUser().getId())) {
                    share.setAmount(share.getAmount().add(optionalShare.get().getAmount()));
                }
            });

        } else {

            // Equal parts: calculate new share amount
            int shareUsers = shareList.size();
            BigDecimal shareAmount = payment.getAmount().divide(BigDecimal.valueOf(shareUsers), 2, RoundingMode.HALF_UP);

            BigDecimal amountShareCreator = payment.getAmount().subtract(shareAmount.multiply(BigDecimal.valueOf((shareUsers - 1))));

            shareList.forEach(share ->
            {
                share.setAmount(shareAmount);
                if (share.getUser().getId().equals(payment.getUser().getId())) share.setAmount(amountShareCreator);

            });
        }

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
