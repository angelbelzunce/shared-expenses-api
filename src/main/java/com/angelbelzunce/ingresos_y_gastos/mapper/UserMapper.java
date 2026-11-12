package com.angelbelzunce.ingresos_y_gastos.mapper;

import com.angelbelzunce.ingresos_y_gastos.dto.UserRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.UserResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request){
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());

        return user;
    }

    public UserResponse toResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
                );
    }

}
