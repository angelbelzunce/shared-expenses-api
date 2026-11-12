package com.angelbelzunce.ingresos_y_gastos.controller;

import com.angelbelzunce.ingresos_y_gastos.dto.GroupMemberResponse;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupResponse;
import com.angelbelzunce.ingresos_y_gastos.dto.PaymentResponse;
import com.angelbelzunce.ingresos_y_gastos.enums.PaymentStatus;
import com.angelbelzunce.ingresos_y_gastos.service.GroupService;
import com.angelbelzunce.ingresos_y_gastos.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<GroupResponse>> findAll() {

        return ResponseEntity.ok(groupService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> findById(@PathVariable Long id) {

        return ResponseEntity.ok(groupService.findById(id));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<GroupMemberResponse>> findMembers(@PathVariable Long id) {

        return ResponseEntity.ok(groupService.findMembers(id));
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<PaymentResponse>> findPayments(@PathVariable Long id,
                                                              @AuthenticationPrincipal Long userId,
                                                              @RequestParam(required = false) PaymentStatus status) {

        return ResponseEntity.ok(paymentService.findPaymentsByGroup(id, userId, status));
    }

    @PostMapping
    public ResponseEntity<GroupResponse> create(@Valid @RequestBody GroupRequest request, @AuthenticationPrincipal Long userId) {

        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.create(request, userId));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponse> update(@PathVariable Long groupId, @Valid @RequestBody GroupRequest request, @AuthenticationPrincipal Long userId) {

        return ResponseEntity.ok(groupService.update(groupId, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Long userId) {

        groupService.delete(id, userId);

        return ResponseEntity.noContent().build();
    }

}
