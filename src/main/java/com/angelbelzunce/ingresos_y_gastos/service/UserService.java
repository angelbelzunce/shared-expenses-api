package com.angelbelzunce.ingresos_y_gastos.service;

import com.angelbelzunce.ingresos_y_gastos.dto.UserRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.UserResponse;

public interface UserService {

    UserResponse findById(Long id);
    UserResponse create(UserRequest request);

}
