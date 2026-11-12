package com.angelbelzunce.ingresos_y_gastos.repository;

import com.angelbelzunce.ingresos_y_gastos.entity.Payment;
import com.angelbelzunce.ingresos_y_gastos.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
            SELECT s.payment
            FROM PaymentShare s
            WHERE s.user.id = :userId
            AND s.payment.group.id = :groupId
            AND (:status IS NULL or s.payment.paymentStatus = :status)
            """)
    List<Payment> findPaymentsByGroupAndUser(Long groupId, Long userId, PaymentStatus status);
}
