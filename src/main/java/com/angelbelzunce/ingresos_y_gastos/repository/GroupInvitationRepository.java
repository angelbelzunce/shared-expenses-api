package com.angelbelzunce.ingresos_y_gastos.repository;

import com.angelbelzunce.ingresos_y_gastos.dto.GroupInvitationRequest;
import com.angelbelzunce.ingresos_y_gastos.entity.GroupInvitation;
import com.angelbelzunce.ingresos_y_gastos.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

    public List<GroupInvitation> findByInvitedUserIdAndInvitationStatus(Long userId, InvitationStatus status);
}
