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
    public ResponseEntity<List<PaymentResponse>> findPayments(@PathVariable Long id, @RequestParam(required = false) PaymentStatus status) {
        return ResponseEntity.ok(paymentService.findByGroup(id, status));
    }

    @PostMapping
    public ResponseEntity<GroupResponse> create(@Valid @RequestBody GroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupResponse> update(@PathVariable Long id, @Valid @RequestBody GroupRequest request) {
        return ResponseEntity.ok(groupService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) {
        groupService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

}
