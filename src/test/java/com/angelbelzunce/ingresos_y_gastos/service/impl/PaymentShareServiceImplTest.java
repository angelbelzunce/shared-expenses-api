package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.entity.Group;
import com.angelbelzunce.ingresos_y_gastos.entity.Payment;
import com.angelbelzunce.ingresos_y_gastos.exception.UserNotInGroupException;
import com.angelbelzunce.ingresos_y_gastos.mapper.PaymentShareMapper;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupMemberRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.PaymentRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.PaymentShareRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    public void settle_userNotInGroup_throwsUserNotInGroupException(){

        Group group = new Group();
        group.setId(1L);
        Payment payment = new Payment();
        payment.setId(10L);
        payment.setGroup(group);

        when(paymentRepository.findById(10L))
                .thenReturn(Optional.of(payment));

        when(groupMemberRepository.existsByGroupIdAndUserId(1L, 5L))
                .thenReturn(false);

        assertThrows(UserNotInGroupException.class, () -> paymentShareServiceImpl.settle(payment.getId(), 7L, 5L));

        verify(paymentShareRepository, never()).save(any());

    }

    @Test
    public void rejectSettle_userNotInGroup_throwsUserNotInGroupException(){

        Group group = new Group();
        group.setId(1L);
        Payment payment = new Payment();
        payment.setId(10L);
        payment.setGroup(group);

        when(paymentRepository.findById(10L))
                .thenReturn(Optional.of(payment));

        when(groupMemberRepository.existsByGroupIdAndUserId(1L, 5L))
                .thenReturn(false);

        assertThrows(UserNotInGroupException.class, () -> paymentShareServiceImpl.rejectSettle(payment.getId(), 7L, 5L));

        verify(paymentShareRepository, never()).save(any());

    }

    @Test
    public void confirmSettle_userNotInGroup_throwsUserNotInGroupException(){

        Group group = new Group();
        group.setId(1L);
        Payment payment = new Payment();
        payment.setId(10L);
        payment.setGroup(group);

        when(paymentRepository.findById(10L))
                .thenReturn(Optional.of(payment));

        when(groupMemberRepository.existsByGroupIdAndUserId(1L, 5L))
                .thenReturn(false);

        assertThrows(UserNotInGroupException.class, () -> paymentShareServiceImpl.confirmSettle(payment.getId(), 7L, 5L));

        verify(paymentShareRepository, never()).save(any());

    }

    @Test
    public void undoConfirmedShare_userNotInGroup_throwsUserNotInGroupException(){

        Group group = new Group();
        group.setId(1L);
        Payment payment = new Payment();
        payment.setId(10L);
        payment.setGroup(group);

        when(paymentRepository.findById(10L))
                .thenReturn(Optional.of(payment));

        when(groupMemberRepository.existsByGroupIdAndUserId(1L, 5L))
                .thenReturn(false);

        assertThrows(UserNotInGroupException.class, () -> paymentShareServiceImpl.undoConfirmedShare(payment.getId(), 7L, 5L));

        verify(paymentShareRepository, never()).save(any());

    }


}
