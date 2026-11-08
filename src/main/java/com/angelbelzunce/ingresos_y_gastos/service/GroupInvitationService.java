package com.angelbelzunce.ingresos_y_gastos.service;

import com.angelbelzunce.ingresos_y_gastos.dto.GroupInvitationRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupInvitationResponse;

import java.util.List;

public interface GroupInvitationService {

    GroupInvitationResponse create(GroupInvitationRequest request);
    List<GroupInvitationResponse> findMyPendingInvitations(Long userId);
    GroupInvitationResponse accept(Long invitationId, Long userId);
    GroupInvitationResponse reject(Long invitationId, Long userId);
}
