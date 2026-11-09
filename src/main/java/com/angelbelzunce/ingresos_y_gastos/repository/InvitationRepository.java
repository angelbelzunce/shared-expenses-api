package com.angelbelzunce.ingresos_y_gastos.repository;

import com.angelbelzunce.ingresos_y_gastos.entity.Invitation;
import com.angelbelzunce.ingresos_y_gastos.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    public List<Invitation> findByInvitedUserIdAndInvitationStatus(Long userId, InvitationStatus status);
}
