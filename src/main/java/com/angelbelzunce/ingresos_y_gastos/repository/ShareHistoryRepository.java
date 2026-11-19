package com.angelbelzunce.ingresos_y_gastos.repository;

import com.angelbelzunce.ingresos_y_gastos.entity.ShareHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareHistoryRepository extends JpaRepository<ShareHistory, Long> {

    List<ShareHistory> findByShare_Payment_IdOrderByModifiedAt(Long paymentId);
}
