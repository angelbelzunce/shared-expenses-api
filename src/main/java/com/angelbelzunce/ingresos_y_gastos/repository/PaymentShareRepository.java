package com.angelbelzunce.ingresos_y_gastos.repository;

import com.angelbelzunce.ingresos_y_gastos.entity.PaymentShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentShareRepository extends JpaRepository<PaymentShare, Long> {

    List<PaymentShare> findByPaymentId(Long paymentId);

    Optional<PaymentShare> findByPaymentIdAndUserId(Long paymentId, Long userId);

    boolean existsByPaymentIdAndUserId(Long paymentId, Long userId);

    List<PaymentShare> findByUserId(Long userId);

    void deleteByPaymentId(Long paymentId);
}
