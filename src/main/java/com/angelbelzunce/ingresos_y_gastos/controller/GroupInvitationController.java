package com.angelbelzunce.ingresos_y_gastos.controller;

import com.angelbelzunce.ingresos_y_gastos.dto.GroupInvitationRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupInvitationResponse;
import com.angelbelzunce.ingresos_y_gastos.service.GroupInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class GroupInvitationController {

    private final GroupInvitationService groupInvitationService;

    @PostMapping
    public ResponseEntity<GroupInvitationResponse> create(@Valid @RequestBody GroupInvitationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupInvitationService.create(request));
    }

    // TODO: take userId from authentication
    @GetMapping
    public ResponseEntity<List<GroupInvitationResponse>> findPendingInvitations(@RequestParam Long userId) {
        return ResponseEntity.ok(groupInvitationService.findMyPendingInvitations(userId));
    }

    // TODO: take userId from authentication
    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<GroupInvitationResponse> accept(@PathVariable Long invitationId, @RequestParam Long userId) {
        return ResponseEntity.ok(groupInvitationService.accept(invitationId, userId));
    }

    // TODO: take userId from authentication
    @PostMapping("/{invitationId}/reject")
    public ResponseEntity<GroupInvitationResponse> reject(@PathVariable Long invitationId, @RequestParam Long userId) {
        return ResponseEntity.ok(groupInvitationService.reject(invitationId, userId));
    }
}
