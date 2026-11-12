package com.angelbelzunce.ingresos_y_gastos.controller;

import com.angelbelzunce.ingresos_y_gastos.dto.InvitationRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.InvitationResponse;
import com.angelbelzunce.ingresos_y_gastos.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping
    public ResponseEntity<InvitationResponse> create(@Valid @RequestBody InvitationRequest request, @AuthenticationPrincipal Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invitationService.create(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<InvitationResponse>> findPendingInvitations(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(invitationService.findMyPendingInvitations(userId));
    }

    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<InvitationResponse> accept(@PathVariable Long invitationId, @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(invitationService.accept(invitationId, userId));
    }

    @PostMapping("/{invitationId}/reject")
    public ResponseEntity<InvitationResponse> reject(@PathVariable Long invitationId, @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(invitationService.reject(invitationId, userId));
    }
}
