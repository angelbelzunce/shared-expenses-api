package com.angelbelzunce.ingresos_y_gastos.controller;

import com.angelbelzunce.ingresos_y_gastos.dto.LoginRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.LoginResponse;
import com.angelbelzunce.ingresos_y_gastos.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){

        return ResponseEntity.ok(authService.login(request));
    }
}
