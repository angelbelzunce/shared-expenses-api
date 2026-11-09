package com.angelbelzunce.ingresos_y_gastos.repository;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.Payment;
import com.angelbelzunce.ingresos_y_gastos.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByGroupId(Long groupId);
    List<Payment> findByGroupIdAndPaymentStatus(Long groupId, PaymentStatus paymentStatus);
}
