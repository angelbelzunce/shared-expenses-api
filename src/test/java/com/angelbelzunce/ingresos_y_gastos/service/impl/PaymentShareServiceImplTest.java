package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.entity.Payment;
import com.angelbelzunce.ingresos_y_gastos.entity.PaymentShare;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.enums.PaymentStatus;
import com.angelbelzunce.ingresos_y_gastos.enums.ShareStatus;
import com.angelbelzunce.ingresos_y_gastos.enums.TransactionType;
import com.angelbelzunce.ingresos_y_gastos.exception.*;
import com.angelbelzunce.ingresos_y_gastos.mapper.PaymentShareMapper;
import com.angelbelzunce.ingresos_y_gastos.repository.PaymentRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.PaymentShareRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.UserRepository;
import com.angelbelzunce.ingresos_y_gastos.service.ShareHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    private PaymentShareMapper paymentShareMapper;
    @Mock
    private ShareHistoryService historyService;

    @InjectMocks
    private PaymentShareServiceImpl paymentShareServiceImpl;

    private Payment payment;
    private User paymentCreator;
    private User shareOwner;
    private User outsider;
    private PaymentShare paymentShare;

    @BeforeEach
    public void setUp() {

        paymentCreator = new User();
        paymentCreator.setId(200L);

        shareOwner = new User();
        shareOwner.setId(300L);

        outsider = new User();
        outsider.setId(400L);

        payment = new Payment();
        payment.setId(10L);
        payment.setUser(paymentCreator);
        payment.setTransactionType(TransactionType.EXPENSE);

        paymentShare = new PaymentShare();
        paymentShare.setUser(shareOwner);
        paymentShare.setStatus(ShareStatus.PENDING);

    }

    @Nested
    public class Settle {

        @Test
        public void settle_paymentNotExists_throwsPaymentNotFoundException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(PaymentNotFoundException.class,
                    () -> paymentShareServiceImpl.settle(payment.getId(), shareOwner.getId(), paymentCreator.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void settle_userIsNotInPayment_throwsUserNotInPaymentException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), outsider.getId()))
                    .thenReturn(false);

            assertThrows(UserNotInPaymentException.class,
                    () -> paymentShareServiceImpl.settle(payment.getId(), shareOwner.getId(), outsider.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void settle_shareNotExists_throwsPaymentShareNotFoundException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), paymentCreator.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(PaymentShareNotFoundException.class,
                    () -> paymentShareServiceImpl.settle(payment.getId(), shareOwner.getId(), paymentCreator.getId()));

            verify(paymentShareRepository, never()).save(any());
        }

        @Test
        public void settle_shareIsNotPending_throwsInvalidShareStatusException() {

            paymentShare.setStatus(ShareStatus.SETTLED);

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), paymentCreator.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.of(paymentShare));

            assertThrows(InvalidShareStatusException.class,
                    () -> paymentShareServiceImpl.settle(payment.getId(), shareOwner.getId(), paymentCreator.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void settle_userIsNotCreatorOfThisIncome_throwsIsNotShareCreatorException() {

            payment.setTransactionType(TransactionType.INCOME);

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.of(paymentShare));

            assertThrows(IsNotShareCreatorException.class,
                    () -> paymentShareServiceImpl.settle(payment.getId(), shareOwner.getId(), shareOwner.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void settle_userIsNotOwnerOfThisExpense_throwsIsNotShareOwnerException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), paymentCreator.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.of(paymentShare));

            assertThrows(IsNotShareOwnerException.class,
                    () -> paymentShareServiceImpl.settle(payment.getId(), shareOwner.getId(), paymentCreator.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void settle_shareIsSettled() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.of(paymentShare));

            when(paymentShareRepository.save(paymentShare))
                    .thenReturn(paymentShare);

            when(userRepository.getReferenceById(shareOwner.getId()))
                    .thenReturn(shareOwner);

            paymentShareServiceImpl.settle(payment.getId(), shareOwner.getId(), shareOwner.getId());

            verify(paymentShareRepository, times(1)).save(paymentShare);

            assertEquals(ShareStatus.SETTLED, paymentShare.getStatus());
            assertNotNull(paymentShare.getPaidAt());

            verify(historyService, times(1)).create(paymentShare, shareOwner, ShareStatus.PENDING, ShareStatus.SETTLED);

        }

    }

    @Nested
    public class RejectSettle {

        @Test
        public void rejectSettle_paymentNotExists_throwsPaymentNotFoundException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(PaymentNotFoundException.class,
                    () -> paymentShareServiceImpl.rejectSettle(payment.getId(), shareOwner.getId(), paymentCreator.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void rejectSettle_userIsNotInPayment_throwsUserNotInPaymentException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), outsider.getId()))
                    .thenReturn(false);

            assertThrows(UserNotInPaymentException.class,
                    () -> paymentShareServiceImpl.rejectSettle(payment.getId(), shareOwner.getId(), outsider.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void rejectSettle_paymentShareNotExists_throwsPaymentShareNotFoundException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), paymentCreator.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(PaymentShareNotFoundException.class,
                    () -> paymentShareServiceImpl.rejectSettle(payment.getId(), shareOwner.getId(), paymentCreator.getId()));

            verify(paymentShareRepository, never()).save(any());
        }

        @Test
        public void rejectSettle_userIsNotExpenseShareCreator_throwsIsNotShareCreatorException(){

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.of(paymentShare));

            assertThrows(IsNotShareCreatorException.class,
                    () -> paymentShareServiceImpl.rejectSettle(payment.getId(), shareOwner.getId(), shareOwner.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void rejectSettle_userIsNotIncomeShareOwner_throwsIsNotShareOwnerException(){

            payment.setTransactionType(TransactionType.INCOME);

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), paymentCreator.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.of(paymentShare));

            assertThrows(IsNotShareOwnerException.class,
                    () -> paymentShareServiceImpl.rejectSettle(payment.getId(), shareOwner.getId(), paymentCreator.getId()));

            verify(paymentShareRepository, never()).save(any());

        }


        @Test
        public void rejectSettle_shareIsNotSettled_throwsInvalidShareStatusException(){

            paymentShare.setStatus(ShareStatus.PENDING);

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), paymentCreator.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.of(paymentShare));

            assertThrows(InvalidShareStatusException.class,
                    () -> paymentShareServiceImpl.rejectSettle(payment.getId(), shareOwner.getId(), paymentCreator.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void rejectSettle_shareStatusIsPending(){

            paymentShare.setStatus(ShareStatus.SETTLED);

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), paymentCreator.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.of(paymentShare));

            when(paymentShareRepository.save(paymentShare))
                    .thenReturn(paymentShare);

            when(userRepository.getReferenceById(paymentCreator.getId()))
                    .thenReturn(paymentCreator);

            paymentShareServiceImpl.rejectSettle(payment.getId(), shareOwner.getId(), paymentCreator.getId());

            assertEquals(ShareStatus.PENDING, paymentShare.getStatus());

            verify(paymentShareRepository, times(1)).save(any());

            verify(historyService, times(1)).create(paymentShare, paymentCreator, ShareStatus.SETTLED, ShareStatus.PENDING);

        }
    }

    @Nested
    public class ConfirmSettle {

        @Test
        public void confirmSettle_paymentNotExists_throwsPaymentNotFoundException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(PaymentNotFoundException.class,
                    () -> paymentShareServiceImpl.confirmSettle(payment.getId(), shareOwner.getId(), paymentCreator.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void confirmSettle_userNotInPayment_throwsUserNotInPaymentException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), outsider.getId()))
                    .thenReturn(false);

            assertThrows(UserNotInPaymentException.class,
                    () -> paymentShareServiceImpl.confirmSettle(payment.getId(), shareOwner.getId(), outsider.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void confirmSettle_shareNotFound_throwsPaymentShareNotFoundException(){

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(PaymentShareNotFoundException.class,
                    () -> paymentShareServiceImpl.confirmSettle(payment.getId(), shareOwner.getId(), shareOwner.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void confirmSettle_userIsNotShareCreator_throwsIsNotShareCreatorException(){

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.of(paymentShare));

            assertThrows(IsNotShareCreatorException.class,
                    () -> paymentShareServiceImpl.confirmSettle(payment.getId(), shareOwner.getId(), shareOwner.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void confirmSettle_userIsNotShareOwner_throwsIsNotShareOwnerException(){

            payment.setTransactionType(TransactionType.INCOME);

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), paymentCreator.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.of(paymentShare));

            assertThrows(IsNotShareOwnerException.class,
                    () -> paymentShareServiceImpl.confirmSettle(payment.getId(), shareOwner.getId(), paymentCreator.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void confirmSettle_shareIsNotSettled_throwsInvalidShareStatusException(){

            payment.setTransactionType(TransactionType.INCOME);
            paymentShare.setStatus(ShareStatus.PENDING);

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.of(paymentShare));

            assertThrows(InvalidShareStatusException.class,
                    () -> paymentShareServiceImpl.confirmSettle(payment.getId(), shareOwner.getId(), shareOwner.getId()));

            verify(paymentShareRepository, never()).save(any());

        }

        @Test
        public void confirmSettle_shareIsConfirmed(){

            payment.setTransactionType(TransactionType.INCOME);
            paymentShare.setStatus(ShareStatus.SETTLED);

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.of(paymentShare));

            when(paymentShareRepository.save(paymentShare))
                    .thenReturn(paymentShare);

            when(userRepository.getReferenceById(shareOwner.getId()))
                    .thenReturn(shareOwner);

            paymentShareServiceImpl.confirmSettle(payment.getId(), shareOwner.getId(), shareOwner.getId());

            assertEquals(ShareStatus.CONFIRMED, paymentShare.getStatus());

            verify(paymentShareRepository, times(1)).save(any());

            verify(historyService, times(1)).create(paymentShare, shareOwner, ShareStatus.SETTLED, ShareStatus.CONFIRMED);

        }

        @Test
        public void confirmSettle_allSharesConfirmed_setsPaymentPaid(){

            payment.setTransactionType(TransactionType.INCOME);
            paymentShare.setStatus(ShareStatus.SETTLED);

            PaymentShare creatorShare = new PaymentShare();
            creatorShare.setUser(paymentCreator);
            creatorShare.setStatus(ShareStatus.CONFIRMED);

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(true);

            when(paymentShareRepository.findByPaymentIdAndUserId(payment.getId(), shareOwner.getId()))
                    .thenReturn(Optional.of(paymentShare));

            when(paymentShareRepository.findByPaymentId(payment.getId()))
                    .thenReturn(List.of(creatorShare, paymentShare));

            paymentShareServiceImpl.confirmSettle(payment.getId(), shareOwner.getId(), shareOwner.getId());

            assertEquals(PaymentStatus.PAID, payment.getPaymentStatus());

            verify(paymentRepository, times(1)).save(payment);

        }

    }

    @Nested
    public class UndoConfirmedShare {

        @Test
        public void undoConfirmedShare_userNotInPayment_throwsUserNotInPaymentException() {

            when(paymentRepository.findById(payment.getId()))
                    .thenReturn(Optional.of(payment));

            when(paymentShareRepository.existsByPaymentIdAndUserId(payment.getId(), outsider.getId()))
                    .thenReturn(false);

            assertThrows(UserNotInPaymentException.class,
                    () -> paymentShareServiceImpl.undoConfirmedShare(payment.getId(), shareOwner.getId(), outsider.getId()));

            verify(paymentShareRepository, never()).save(any());

        }
    }

}
