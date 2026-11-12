package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.LoginRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.LoginResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.repository.UserRepository;
import com.angelbelzunce.ingresos_y_gastos.security.JwtService;
import com.angelbelzunce.ingresos_y_gastos.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid user or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new BadCredentialsException("Invalid user or password");
        }

        return new LoginResponse(jwtService.generateToken(user));
    }
}
