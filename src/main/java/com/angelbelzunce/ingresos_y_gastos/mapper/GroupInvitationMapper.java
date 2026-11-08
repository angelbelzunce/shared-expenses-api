package com.angelbelzunce.ingresos_y_gastos.mapper;

import com.angelbelzunce.ingresos_y_gastos.dto.GroupInvitationRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupInvitationResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.GroupInvitation;
import org.springframework.stereotype.Component;

@Component
public class GroupInvitationMapper {

    public GroupInvitationResponse toResponse(GroupInvitation groupInvitation) {
        return new GroupInvitationResponse(
                groupInvitation.getId(),
                groupInvitation.getGroup().getId(),
                groupInvitation.getInvitedUser().getId(),
                groupInvitation.getInvitedBy().getId(),
                groupInvitation.getInvitationStatus(),
                groupInvitation.getCreatedAt()
        );
    }
}
