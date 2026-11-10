package com.angelbelzunce.ingresos_y_gastos.controller;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentShareResponse;
import com.angelbelzunce.ingresos_y_gastos.service.PaymentShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/{paymentId}/shares")
public class PaymentShareController {

    private final PaymentShareService paymentShareService;

    @GetMapping
    public ResponseEntity<List<PaymentShareResponse>> findByPaymentId(@PathVariable Long paymentId){
        return ResponseEntity.ok(paymentShareService.findByPaymentId(paymentId));
    }
    @GetMapping("/{userId}")
    public ResponseEntity<PaymentShareResponse> findByPaymentIdAndUserId(@PathVariable Long paymentId, @PathVariable Long userId){
        return ResponseEntity.ok(paymentShareService.findByPaymentIdAndUserId(paymentId, userId));
    }
    /*

    @DeleteMapping
    public ResponseEntity<PaymentShareResponse> deleteByPaymentId(@PathVariable Long paymentId){
        paymentShareService.deleteByPaymentId(paymentId);
        return ResponseEntity.noContent().build();
    }

     */
}
