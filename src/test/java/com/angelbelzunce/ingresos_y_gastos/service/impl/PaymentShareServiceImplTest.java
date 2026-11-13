package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.entity.Group;
import com.angelbelzunce.ingresos_y_gastos.entity.Payment;
import com.angelbelzunce.ingresos_y_gastos.entity.PaymentShare;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.enums.ShareStatus;
import com.angelbelzunce.ingresos_y_gastos.enums.TransactionType;
import com.angelbelzunce.ingresos_y_gastos.exception.*;
import com.angelbelzunce.ingresos_y_gastos.mapper.PaymentShareMapper;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupMemberRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.PaymentRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.PaymentShareRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentShareServiceImplTest {

    @Mock
    private PaymentShareRepository paymentShareRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupMemberRepository groupMemberRepository;
    @Mock
    private PaymentShareMapper paymentShareMapper;

    @InjectMocks
    private PaymentShareServiceImpl paymentShareServiceImpl;

    private Group group;
    private Payment payment;
    private User creator;
    private User shareUser;
    private PaymentShare paymentShare;

    @BeforeEach
    public void setUp() {

        creator = new User();
        creator.setId(200L);

        shareUser = new User();
        shareUser.setId(300L);

        group = new Group();
        group.setId(1L);

        payment = new Payment();
        payment.setId(10L);
        payment.setUser(creator);
        payment.setGroup(group);
        payment.setTransactionType(TransactionType.EXPENSE);

        paymentShare = new PaymentShare();
        paymentShare.setUser(shareUser);
        paymentShare.setStatus(ShareStatus.PENDING);

    }

    @Nested
    public class Settle {

        @Test
        public void settle_paymentNotExists_throwsPaymentNotFoundException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(PaymentNotFoundException.class,
                    () -> paymentShareServiceImpl.settle(payment.getId(), shareUser.getId(), creator.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void settle_userNotInGroup_throwsUserNotInGroupException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(groupMemberRepository.existsByGroupIdAndUserId(group.getId(), shareUser.getId()))
                    .thenReturn(false);

            assertThrows(UserNotInGroupException.class,
                    () -> paymentShareServiceImpl.settle(payment.getId(), shareUser.getId(), shareUser.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void settle_paymentShareNotExists_throwsPaymentShareNotFoundException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(groupMemberRepository.existsByGroupIdAndUserId(group.getId(), creator.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareUser.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(PaymentShareNotFoundException.class,
                    () -> paymentShareServiceImpl.settle(payment.getId(), shareUser.getId(), creator.getId()));

            verify(paymentShareRepository, never()).save(any());
        }

        @Test
        public void settle_paymentShareIsNotPending_throwsNotPendingShareException() {

            paymentShare.setStatus(ShareStatus.SETTLED);

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(groupMemberRepository.existsByGroupIdAndUserId(group.getId(), creator.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareUser.getId()))
                    .thenReturn(Optional.of(paymentShare));

            assertThrows(NotPendingShareException.class,
                    () -> paymentShareServiceImpl.settle(payment.getId(), shareUser.getId(), creator.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void settle_isNotCreatorOfThisIncome_throwsIsNotShareCreatorException() {

            payment.setTransactionType(TransactionType.INCOME);

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(groupMemberRepository.existsByGroupIdAndUserId(group.getId(), shareUser.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareUser.getId()))
                    .thenReturn(Optional.of(paymentShare));

            assertThrows(IsNotShareCreatorException.class,
                    () -> paymentShareServiceImpl.settle(payment.getId(), shareUser.getId(), shareUser.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void settle_isNotOwnerOfThisExpense_throwsIsNotShareOwnerException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(groupMemberRepository.existsByGroupIdAndUserId(group.getId(), creator.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareUser.getId()))
                    .thenReturn(Optional.of(paymentShare));

            assertThrows(IsNotShareOwnerException.class,
                    () -> paymentShareServiceImpl.settle(payment.getId(), shareUser.getId(), creator.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void settle_setShareStatusSettled() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(groupMemberRepository.existsByGroupIdAndUserId(group.getId(), shareUser.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareUser.getId()))
                    .thenReturn(Optional.of(paymentShare));

            paymentShareServiceImpl.settle(payment.getId(), shareUser.getId(), shareUser.getId());

            verify(paymentShareRepository, times(1)).save(paymentShare);

            assertEquals(ShareStatus.SETTLED, paymentShare.getStatus());
            assertNotNull(paymentShare.getPaidAt());

        }

    }

    @Nested
    public class RejectSettle {
        @Test
        public void rejectSettle_userNotInGroup_throwsUserNotInGroupException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(groupMemberRepository.existsByGroupIdAndUserId(group.getId(), shareUser.getId()))
                    .thenReturn(false);

            assertThrows(UserNotInGroupException.class,
                    () -> paymentShareServiceImpl.rejectSettle(payment.getId(), shareUser.getId(), shareUser.getId()));

            verify(paymentShareRepository, never()).save(any());

        }
    }

    @Nested
    public class ConfirmSettle {

        @Test
        public void confirmSettle_userNotInGroup_throwsUserNotInGroupException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(groupMemberRepository.existsByGroupIdAndUserId(group.getId(), shareUser.getId()))
                    .thenReturn(false);

            assertThrows(UserNotInGroupException.class,
                    () -> paymentShareServiceImpl.confirmSettle(payment.getId(), shareUser.getId(), shareUser.getId()));

            verify(paymentShareRepository, never()).save(any());

        }
    }

    @Nested
    public class UndoConfirmedShare {

        @Test
        public void undoConfirmedShare_userNotInGroup_throwsUserNotInGroupException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(groupMemberRepository.existsByGroupIdAndUserId(group.getId(), shareUser.getId()))
                    .thenReturn(false);

            assertThrows(UserNotInGroupException.class,
                    () -> paymentShareServiceImpl.undoConfirmedShare(payment.getId(), shareUser.getId(), shareUser.getId()));

            verify(paymentShareRepository, never()).save(any());

        }
    }

}
