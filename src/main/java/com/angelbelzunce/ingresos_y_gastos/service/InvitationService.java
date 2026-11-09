package com.angelbelzunce.ingresos_y_gastos.service;

import com.angelbelzunce.ingresos_y_gastos.dto.InvitationRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.InvitationResponse;

import java.util.List;

public interface InvitationService {

    InvitationResponse create(InvitationRequest request);
    List<InvitationResponse> findMyPendingInvitations(Long userId);
    InvitationResponse accept(Long invitationId, Long userId);
    InvitationResponse reject(Long invitationId, Long userId);
}
