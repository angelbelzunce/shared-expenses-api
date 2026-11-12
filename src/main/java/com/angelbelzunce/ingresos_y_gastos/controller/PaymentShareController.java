package com.angelbelzunce.ingresos_y_gastos.controller;

import com.angelbelzunce.ingresos_y_gastos.dto.PaymentShareResponse;
import com.angelbelzunce.ingresos_y_gastos.service.PaymentShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/{paymentId}/shares")
public class PaymentShareController {

    private final PaymentShareService paymentShareService;

    @GetMapping
    public ResponseEntity<List<PaymentShareResponse>> findByPaymentId(@PathVariable Long paymentId) {

        return ResponseEntity.ok(paymentShareService.findByPaymentId(paymentId));

    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<PaymentShareResponse> findByPaymentIdAndUserId(@PathVariable Long paymentId,
                                                                         @PathVariable Long ownerId) {

        return ResponseEntity.ok(paymentShareService.findByPaymentIdAndUserId(paymentId, ownerId));
    }

    @PatchMapping("/{shareUserId}/settle")
    public ResponseEntity<PaymentShareResponse> settle(@PathVariable Long paymentId,
                                                       @PathVariable Long shareUserId,
                                                       @AuthenticationPrincipal Long userId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentShareService.settle(paymentId, shareUserId, userId));
    }

    @PatchMapping("/{shareUserId}/confirm")
    public ResponseEntity<PaymentShareResponse> confirm(@PathVariable Long paymentId,
                                                        @PathVariable Long shareUserId,
                                                        @AuthenticationPrincipal Long userId
                                                        ) {

        return ResponseEntity.status(HttpStatus.OK).
                body(paymentShareService.confirmSettle(paymentId, shareUserId, userId));
    }

    @PatchMapping("/{shareUserId}/reject")
    public ResponseEntity<PaymentShareResponse> reject(@PathVariable Long paymentId,
                                                        @PathVariable Long shareUserId,
                                                        @AuthenticationPrincipal Long userId
    ) {

        return ResponseEntity.status(HttpStatus.OK).
                body(paymentShareService.rejectSettle(paymentId, shareUserId, userId));
    }

    @PatchMapping("/{shareUserId}/undo")
    public ResponseEntity<PaymentShareResponse> undo(@PathVariable Long paymentId,
                                                       @PathVariable Long shareUserId,
                                                       @AuthenticationPrincipal Long userId
    ) {

        return ResponseEntity.status(HttpStatus.OK).
                body(paymentShareService.undoConfirmedShare(paymentId, shareUserId, userId));
    }
}
