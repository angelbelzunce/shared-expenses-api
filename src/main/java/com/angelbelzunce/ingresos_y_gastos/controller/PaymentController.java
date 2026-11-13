package com.angelbelzunce.ingresos_y_gastos.controller;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.PaymentResponse;
import com.angelbelzunce.ingresos_y_gastos.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest request, @AuthenticationPrincipal Long userId){
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(request, userId));
    }

    @PostMapping("/{paymentId}/leave")
    public ResponseEntity<Void> leave(@PathVariable Long paymentId, @AuthenticationPrincipal Long userId){
        paymentService.leave(paymentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> update(@PathVariable Long paymentId, @AuthenticationPrincipal Long userId, @Valid @RequestBody PaymentRequest request){
        return ResponseEntity.ok(paymentService.update(paymentId, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Long userId){
        paymentService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
