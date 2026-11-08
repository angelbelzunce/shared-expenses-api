package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.UserRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.UserResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.exception.EmailAlreadyExistsException;
import com.angelbelzunce.ingresos_y_gastos.exception.UserNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.mapper.UserMapper;
import com.angelbelzunce.ingresos_y_gastos.repository.UserRepository;
import com.angelbelzunce.ingresos_y_gastos.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse findById(Long id) {
        return userMapper.toResponse(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException(request.email());
        }

        User savedUser = userRepository.save(userMapper.toEntity(request));

        return userMapper.toResponse(savedUser);

    }
}
