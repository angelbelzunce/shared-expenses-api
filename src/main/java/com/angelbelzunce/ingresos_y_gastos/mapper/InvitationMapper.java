package com.angelbelzunce.ingresos_y_gastos.mapper;

import com.angelbelzunce.ingresos_y_gastos.dto.InvitationResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.Invitation;
import org.springframework.stereotype.Component;

@Component
public class InvitationMapper {

    public InvitationResponse toResponse(Invitation invitation) {
        return new InvitationResponse(
                invitation.getId(),
                invitation.getGroup().getId(),
                invitation.getInvitedUser().getId(),
                invitation.getInvitedBy().getId(),
                invitation.getInvitationStatus(),
                invitation.getCreatedAt()
        );
    }
}
