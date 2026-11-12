package com.angelbelzunce.ingresos_y_gastos.service;

import com.angelbelzunce.ingresos_y_gastos.dto.LoginRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
